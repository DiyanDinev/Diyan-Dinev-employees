package com.example.pairs.controller;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.pairs.domain.LoadFile;
import com.example.pairs.domain.LongestPeriod;
import com.example.pairs.domain.ParsedFile;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

@Controller
public class IndexController {
	
	
	@RequestMapping(value ={"/", "/index"})
	String index(Model model) {
		return "index"; 
	}
	
	@RequestMapping(value = "/UploadFile", method = RequestMethod.POST)
	String uploadFile(Model model, @RequestParam("filename") MultipartFile file) {
		  try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
			  
            // create csv bean reader
            @SuppressWarnings({ "unchecked", "rawtypes" })
			CsvToBean<LoadFile> csvToBean = new CsvToBeanBuilder(reader)
	                .withType(LoadFile.class)
	                .build();
     
            // convert `CsvToBean` object to list of users
            List<LoadFile> records = csvToBean.parse();
             
            List<ParsedFile> parsedRecords = new ArrayList<ParsedFile>();
            
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            
            
            for(LoadFile lf : records) {
            	ParsedFile pf = new ParsedFile();
            	
            	pf.setEmpId(lf.getEmpId());
            	pf.setProjectId(lf.getProjectId());
            	
            	if(lf.getDateFrom() == null || "NULL".equals(lf.getDateFrom().trim())) {
            		pf.setDateFrom(new Date());
            	} else {
            		pf.setDateFrom(formatter.parse(lf.getDateFrom()));
            	}
            	
            	if("NULL".equals(lf.getDateTo().trim()) || lf.getDateTo() == null) {
            		pf.setDateTo(new Date());
            	} else {
            		pf.setDateTo(formatter.parse(lf.getDateTo()));
            	}
            	
            	parsedRecords.add(pf);
            }
            
            List<LongestPeriod> lp =  new ArrayList<LongestPeriod>();
            
            //loop while have 2 records at a time to compare them
            for (int i = 0; i < parsedRecords.size() - 1; i++) {
                for (int j = i + 1; j < parsedRecords.size(); j++) {
                    if (parsedRecords.get(i).getProjectId() == parsedRecords.get(j).getProjectId() && areEmpDatesTogether(parsedRecords.get(i), parsedRecords.get(j))) {
                    	long daysBetween = countDaysBetweenDates(parsedRecords.get(i),  parsedRecords.get(j));

                        if (daysBetween > 0) {
                        	lp.add(updatePairsWtihLongesrPeriodOfWork(parsedRecords.get(i).getProjectId(), parsedRecords.get(i).getEmpId(), parsedRecords.get(j).getEmpId(), daysBetween));
                        }
                    }
                }
            }

            
            if(lp.size() > 0) {
                long checkPeriod = 0;
                int indexHolder = 0;
                for(int i = 0; i< lp.size(); i++) {
                	if(lp.get(i).getDaysWorked() > checkPeriod) {
                		checkPeriod = lp.get(i).getDaysWorked();
                		indexHolder = i;
                	}
                	
                }
                
                model.addAttribute("record", lp.get(indexHolder));
            } else {
            	model.addAttribute("error", "No pairs Available");
            }
            

        } catch (Exception ex) {
        	System.out.println("Problem loading the file");
        	model.addAttribute("error", "Problem loading the file");
        }
		
		
		return "load_file"; 
	}
	
	// if emp1 start date is before emp2 end date and emp1 end date is after emp2 start date then both have worked together
    private boolean areEmpDatesTogether(ParsedFile emp1, ParsedFile emp2) {
        return (emp1.getDateFrom().compareTo(emp2.getDateTo()) <= 0 && emp1.getDateTo().compareTo(emp2.getDateFrom()) >= 0);      
    }
    
    private long countDaysBetweenDates(ParsedFile emp1, ParsedFile emp2) {
    	Date date1;
    	Date date2;
    	if(emp1.getDateFrom().compareTo(emp2.getDateFrom()) <= 0) {
    		date1 = emp2.getDateFrom();
    	} else { date1 = emp1.getDateFrom();}
    	if(emp1.getDateTo().compareTo(emp2.getDateTo()) <= 0) {
    		date2 = emp2.getDateTo();
    	}else { date2 = emp1.getDateTo();}
    			
    	return  ChronoUnit.DAYS.between(date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }
	
	
	public LongestPeriod updatePairsWtihLongesrPeriodOfWork(int projId, int emp1Id, int emp2Id, long daysTogether){
		LongestPeriod lp = new LongestPeriod();
		lp.setDaysWorked(daysTogether);
		lp.setEmp1(emp1Id);
		lp.setEmp2(emp2Id);
		lp.setProjId(projId);
		
		return lp;
	}

}
