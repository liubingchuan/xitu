package com.xitu.app.service.es;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.ui.Model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xitu.app.annotation.AggQuery;
import com.xitu.app.annotation.CrossQuery;
import com.xitu.app.annotation.DontMapping;
import com.xitu.app.annotation.SingleQuery;
import com.xitu.app.model.Jiance;
import com.xitu.app.utils.ThreadLocalUtil;

public abstract class AbstractESHttpService implements ESHttpService {

	@Value("${es.endpoint}")
	private String endpoint;

	protected abstract Class<?> getEntityClass();

	@Override
	public void execute(int pageIndex, int pageSize, String...args) {
		convert(getHttpClient().execute(composeDSL(pageIndex, pageSize, args)));
	}

	public ESHttpClient getHttpClient() {
		String url = endpoint;
		String indexName = "";
		String type = "";
		ESHttpClient client = new ESHttpClient(url);
		Document anno = getEntityClass().getAnnotation(Document.class);
		if (anno != null) {
			Method[] met = anno.annotationType().getDeclaredMethods();
			for (Method me : met) {
				if (!me.isAccessible()) {
					me.setAccessible(true);
				}
				try {
					if (me.getName().equals("indexName")) {
						indexName = (String) me.invoke(anno, null);
					} else if (me.getName().equals("type")) {
						type = (String) me.invoke(anno, null);
					}
					System.out.println(me.invoke(anno, null));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			client = new ESHttpClient(url, indexName, type);
		}
		return client;
	}

	public String composeDSL(int pageIndex, int pageSize, String...args) {
		List<Field> fields = getFields(getEntityClass());
		List<String> crossedFields = new ArrayList<String>();
		List<String> singleFields = new ArrayList<String>();
		JSONObject query = new JSONObject();
		JSONObject aggs = new JSONObject();
		for (Field f : fields) {
			AggQuery aggQuery = f.getAnnotation(AggQuery.class);
			CrossQuery crossQuery = f.getAnnotation(CrossQuery.class);
			SingleQuery singleQuery = f.getAnnotation(SingleQuery.class);
			String fieldName = f.getName();
			if (aggQuery != null) {
				JSONObject fz = new JSONObject();
				JSONObject terms = new JSONObject();
				int size = aggQuery.size();
				fz.put("field", fieldName);
				fz.put("size", size);
				terms.put("terms", fz);
				aggs.put(fieldName, terms);
			} else if (crossQuery != null) {
				crossedFields.add(fieldName);
			} else if(singleQuery != null) {
				singleFields.add(fieldName);
			}
		}
		query.put("aggs",aggs);
		JSONObject param = new JSONObject();
		JSONArray should = new JSONArray();
		JSONObject multi_match = new JSONObject();
		JSONObject match_all = new JSONObject();
		JSONArray must = new JSONArray();
		JSONObject bool1 = new JSONObject();
    	JSONObject bool2 = new JSONObject();
		JSONObject bool4 = new JSONObject();
    	JSONObject bool3 = new JSONObject();
		if (args == null || args.length==0) {
		    match_all.put("match_all", param);
		    must.add(match_all);
		}else if(args.length == 1){
			param.put("query", args[0]);
			param.put("operator", "and");
			param.put("type", "cross_fields");
			param.put("fields", crossedFields.toArray());
			//param.put("type", "best_fields");
			multi_match.put("multi_match", param);
			should.add(multi_match);
			bool4.put("should", should);
			bool3.put("bool", bool4);
			must.add(bool3);
		}else {
			for(int i=1;i<args.length;i++) {
				if(isNotBlank(args[i])) {
					if(args[i].contains(",")){
						String[] split = args[i].split(",");
						for(String s : split){
							JSONObject match = new JSONObject();
							JSONObject field = new JSONObject();
							field.put(singleFields.get(i-1), s);
							match.put("match", field);
							must.add(match);
						}
					}else{
						JSONObject match = new JSONObject();
						JSONObject field = new JSONObject();
						field.put(singleFields.get(i-1), args[i]);
						match.put("match", field);
						must.add(match);
					}
				}
				
			}
			
		}
		
    	bool2.put("must",must);
    	bool1.put("bool", bool2);
    	query.put("query", bool1);
    	query.put("from",pageIndex);
    	query.put("size", pageSize);
    	System.out.println("query ---  " + query.toString());
    	return query.toString();
	}

	private List<Field> getFields(Class<?> clazz) {
		List<Field> result = new ArrayList<Field>();
		if (clazz != null) {
			Class<?> superClazz = clazz.getSuperclass();
			if (superClazz != Object.class) {
				result.addAll(getFields(superClazz));
			}
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				DontMapping dontMapping = field.getAnnotation(DontMapping.class);
				if (dontMapping == null) {
					result.add(field);
				}
			}
		}
		return result;
	}
	
	public void convert(JSONObject response) {
		JSONObject hits = response.getJSONObject("hits");
		Integer totalCount = hits.getInteger("total"); 
		JSONArray list = hits.getJSONArray("hits");
		Model model = ThreadLocalUtil.get();
		model.addAttribute("list", list.toJavaList(getEntityClass()));
		model.addAttribute("totalCount", totalCount);
		long totalPages = 0L;
		if (totalCount > 0) {
			if(totalCount % 10 == 0){
				totalPages = totalCount/10;
			}else{
				totalPages = totalCount/10 + 1;
			}
		}
		model.addAttribute("totalPages", totalPages);
		JSONObject aggregations = response.getJSONObject("aggregations");
		Set<String> keys = aggregations.keySet();
		for(String key : keys) {
			model.addAttribute(key, aggregations.get(key));
		}
	}
	

}
