package com.xitu.app.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xitu.app.common.R;
import com.xitu.app.mapper.ElementMapper;
import com.xitu.app.model.ElementMaster;
import com.xitu.app.model.ElementSlave;
import com.xitu.app.repository.ExpertRepository;
import com.xitu.app.service.es.PaperService;
import com.xitu.app.service.es.PatentService;
import com.xitu.app.utils.JsonUtil;
import com.xitu.app.utils.ThreadLocalUtil;



@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
@Controller
public class YuansuController {

	private static final Logger logger = LoggerFactory.getLogger(YuansuController.class);
	
	@Autowired
    private ExpertRepository expertRepository;
	
	@Autowired
    private ElementMapper elementMapper;
	
	@Autowired
	private PatentService patentService;
	
	@Autowired
	private PaperService paperService;
	
	@GetMapping(value = "yuansu/xiangqing")
	public String agpeoplecon(@RequestParam String name, Model model) {
		ElementMaster master = null;
		ElementSlave slave = null;
		if(name == null) {
			return "T-yuansu";
		}
		if(name.trim().length()==1) {
			master = elementMapper.selectMasterByName(name);
			List<ElementSlave> slaves = elementMapper.selectSlavesByMaster(master.getMaster());
			model.addAttribute("master", master);
			model.addAttribute("slaves", slaves);
		}else {
			slave = elementMapper.selectSlaveByName(name);
			model.addAttribute("master",slave);
		}
		model.addAttribute("name", name);
		return "T-yuansu";
	}
	
	@ResponseBody
	@RequestMapping(value = "yuansu/xiangqing/related", method = RequestMethod.POST,consumes = "application/json")
	public R related(@RequestBody JSONObject instance, Model model) {
		ThreadLocalUtil.set(model);
		int pageIndex = 0;
		int pageSize = 1;
		int i = 0;//0代表专利；1代表论文；2代表项目；3代表监测
		Set<String> rens = new TreeSet<String>();
		Set<String> units = new TreeSet<String>();
		patentService.execute(pageIndex, pageSize, i,instance.getString("name"));
		Map<String, Object> map = model.asMap();
		JSONArray creators = JsonUtil.parseArray(map.get("creator").toString());
		JSONArray persons = JsonUtil.parseArray(map.get("person").toString());
		for(int j=0;j<creators.size();j++) {
			JSONObject obj = creators.getJSONObject(j);
			rens.add(obj.getString("key"));
		}
		for(int j=0;j<persons.size();j++) {
			JSONObject obj = persons.getJSONObject(j);
			units.add(obj.getString("key"));
		}
		JSONArray patentList = JsonUtil.parseArray(model.asMap().get("list").toString());
		System.out.println(patentList.toJSONString());
		paperService.execute(pageIndex, pageSize, i, instance.getString("name"));
		map = model.asMap();
		JSONArray authors = JsonUtil.parseArray(map.get("author").toString());
		JSONArray institutions = JsonUtil.parseArray(map.get("institution").toString());
		for(int j=0;j<authors.size();j++) {
			JSONObject obj = authors.getJSONObject(j);
			rens.add(obj.getString("key"));
		}
		for(int j=0;j<institutions.size();j++) {
			JSONObject obj = institutions.getJSONObject(j);
			units.add(obj.getString("key"));
		}
		JSONArray paperList = JsonUtil.parseArray(model.asMap().get("list").toString());
		ThreadLocalUtil.remove();		
		return R.ok().put("patentList", patentList).put("paperList", paperList).put("rens", rens).put("units", units);
	}
	
	/**
     * 文件解析
     * */
    @GetMapping(value="element/import")
    @ResponseBody 
    public R importExcel(){
    	
    	Workbook wb =null;
        Sheet sheet = null;
        Row row = null;
        List<Map<String,String>> list = null;
        String cellData = null;
        String filePath = "/Users/liubingchuan/Desktop/abc.xlsx";
        wb = readExcel(filePath);
        if(wb != null){
            //用来存放表中数据
            list = new ArrayList<Map<String,String>>();
            //获取第一个sheet
            sheet = wb.getSheetAt(0);
            //获取最大行数
            int rownum = sheet.getPhysicalNumberOfRows();
            //获取第一行
            row = sheet.getRow(0);
            //获取最大列数
            int colnum = row.getPhysicalNumberOfCells();
            for (int i = 1; i<rownum; i++) {
            	System.out.println("id------>" + i);
                row = sheet.getRow(i);
                if(row !=null){
                	String name = "";
                	String enName = "";
                	String label = "";
                	String master = "";
                	String description = "";
                	String history = "";
                	String method = "";
                	String property = "";
                	String num = "";
                    for (int j=0;j<colnum;j++){
                        cellData = (String) getCellFormatValue(row.getCell(j));
                        if(j==0) {
                        	name = cellData;
                        }else if(j==1){
                        	enName = cellData;
                        }else if(j==2) {
                        	label = cellData;
                        }else if(j==3) {
                        	master = cellData;
                        }else if(j==4) {
                        	description = cellData;
                        }else if(j==5) {
                        	history = cellData;
                        }else if(j==6) {
                        	method = cellData;
                        }else if(j==7) {
                        	property = cellData;
                        }else if(j==8) {
                        	num = cellData;
                        }
                    }
                    if(name != null) {
                    	if(name.trim().length()==1) {
                    		ElementMaster masterElement = new ElementMaster();
                    		masterElement.setName(name);
                    		masterElement.setEnName(enName);
                    		masterElement.setHistory(history);
                    		masterElement.setLabel(label);
                    		masterElement.setMethod(method);
                    		masterElement.setNum(num);
                    		masterElement.setProp(property);
                    		masterElement.setDescription(description);
                    		masterElement.setMaster(master);
                    		elementMapper.insertMaster(masterElement);
                    	}else {
                    		ElementSlave slaveElement = new ElementSlave();
                    		slaveElement.setName(name);
                    		slaveElement.setEnName(enName);
                    		slaveElement.setLabel(label);
                    		slaveElement.setMethod(method);
                    		slaveElement.setProp(property);
                    		slaveElement.setDescription(description);
                    		slaveElement.setMaster(master);
                    		elementMapper.insertSlave(slaveElement);
                    	}
                    }
                }else{
                    break;
                }
            }
        }
    	return R.ok();
    }
    
  //读取excel
    private static Workbook readExcel(String filePath){
        Workbook wb = null;
        if(filePath==null){
            return null;
        }
        String extString = filePath.substring(filePath.lastIndexOf("."));
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
            if(".xls".equals(extString)){
                return wb = new HSSFWorkbook(is);
            }else if(".xlsx".equals(extString)){
                return wb = new XSSFWorkbook(is);
            }else{
                return wb = null;
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wb;
    }
    private static Object getCellFormatValue(Cell cell){
        Object cellValue = null;
        if(cell!=null){
            //判断cell类型
            switch(cell.getCellType()){
            case Cell.CELL_TYPE_NUMERIC:{
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            }
            case Cell.CELL_TYPE_FORMULA:{
                //判断cell是否为日期格式
                if(DateUtil.isCellDateFormatted(cell)){
                    //转换为日期格式YYYY-mm-dd
                    cellValue = cell.getDateCellValue();
                }else{
                    //数字
                    cellValue = String.valueOf(cell.getNumericCellValue());
                }
                break;
            }
            case Cell.CELL_TYPE_STRING:{
                cellValue = cell.getRichStringCellValue().getString();
                break;
            }
            default:
                cellValue = "";
            }
        }else{
            cellValue = "";
        }
        return cellValue;
    }
}
