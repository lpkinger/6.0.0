/**
 * Enterprise Spreadsheet Solutions
 * Copyright(c) FeyaSoft Inc. All right reserved.
 * info@enterpriseSheet.com
 * http://www.enterpriseSheet.com
 * 
 * Licensed under the EnterpriseSheet Commercial License.
 * http://enterprisesheet.com/license.jsp
 * 
 * You need to have a valid license key to access this file.
 */
Ext.onReady(function() {

    Ext.QuickTips.init();
    
    /**
     * Define those 2 methods as global variable
     */
    SHEET_API = Ext.create('EnterpriseSheet.api.SheetAPI', {
        openFileByOnlyLoadDataFlag: true
    });
    
    SHEET_API_HD = SHEET_API.createSheetApp({
	   withoutTitlebar: false,
       withoutSheetbar: false,
       withoutToolbar: false,
       withoutContentbar: false,
       withoutSidebar: false
	});
    
    // this is tab panel include main and details 
    var centralPanel = Ext.create('enterpriseSheet.templates.CenterPanel', {
    });
    	
    Ext.create('Ext.Viewport', {
        layout: 'border',
        items: [ centralPanel],
        listeners: {
	      afterlayout: function(v, layout, eOpts) {
		      // westPanel.selectNode();
	      }
	    }
    });
    
    // =============================================================================================
    // ok inject data now ...
    /**
    var json = {
    		fileName: "To do list",
    		sheets:[{id:1, name:"Tasks", actived:true, color:"orange"},{id:2, name:"Common", actived:false, color:"blue"}],
    		floatings: [{ sheet:1, name:"cdt-1", ftype:"cdt", json: "{name:\"colorbar\",rng:[{span:[1,1,7,100,7],type:1}],opt:{pos:\"rgb(255,0,0)\",neg:\"rgb(0,255,255)\"} }"},{ sheet:1, name:"colGroups", ftype:"colgroup", json: "[{level:1, span:[2,6]}]" }],
    		cells:[
{ sheet: 1, row: 0, col: 0, json: { height: 25, va: "middle"} },
{ sheet: 1, row: 0, col: 1, json: { data: "Task", width: 250, ticon:"address", render:"fuchsiaRender"} },
{ sheet: 1, row: 0, col: 2, json: { data: "Priority", width: 100, dcfg: "{dt:16, ignoreBlank: true, refList: '=Common!$A:$A'}", ticon:"remoteList", render:"lightGreenRender"} },
{ sheet: 1, row: 0, col: 3, json: { data: "Status", width: 120, dcfg: "{dt:16, ignoreBlank: true, refList: '=Common!$B:$B'}", ticon:"remoteList", render:"lightPurpleRender"} },
{ sheet: 1, row: 0, col: 4, json: { data: "Start date", width: 140, drop: "date", fm: "date", dfm: "F d, Y", ticon:"calendar"  } },
{ sheet: 1, row: 0, col: 5, json: { data: "Due date", width: 140, drop: "date", fm: "date", dfm: "F d, Y", ticon:"calendar"  } },
{ sheet: 1, row: 0, col: 6, json: { data: "Attachment", width: 120, dcfg: "{dt:7}", ticon:"attach" } },	
{ sheet: 1, row: 0, col: 7, json: { data: "Process", width: 150, dcfg: "{dt:12, format: '0%'}", ticon:"percent" } },
{ sheet: 1, row: 0, col: 8, json: { data: "Notes", width: 250, dcfg: "{dt:14}",  ticon:"textLong" } },

{ sheet: 1, row: 1, col: 1, json: { data: "First Thing I Need To Do"} },
{ sheet: 1, row: 1, col: 2, json: { data: "Normal"} },
{ sheet: 1, row: 1, col: 3, json: { data: "Not started"} },
{ sheet: 1, row: 1, col: 4, json: { data: "2016-05-01"} },
{ sheet: 1, row: 1, col: 5, json: { data: "2016-05-10"} },
{ sheet: 1, row: 1, col: 7, json: { data: 0.5} },
{ sheet: 1, row: 2, col: 1, json: { data: "Other Thing I Need To Finish"} },
{ sheet: 1, row: 2, col: 2, json: { data: "High"} },
{ sheet: 1, row: 2, col: 3, json: { data: "In progress"} },
{ sheet: 1, row: 2, col: 4, json: { data: "2016-05-20"} },
{ sheet: 1, row: 2, col: 5, json: { data: "2016-07-12"} },
{ sheet: 1, row: 2, col: 7, json: { data: 0.25} },
{ sheet: 1, row: 3, col: 1, json: { data: "Something Else To Get Done"} },
{ sheet: 1, row: 3, col: 2, json: { data: "Low"} },
{ sheet: 1, row: 3, col: 3, json: { data: "In progress"} },
{ sheet: 1, row: 3, col: 4, json: { data: "2016-06-10"} },
{ sheet: 1, row: 3, col: 5, json: { data: "2016-06-12"} },
{ sheet: 1, row: 3, col: 7, json: { data: 0.75} },
{ sheet: 1, row: 4, col: 1, json: { data: "More Errands And Things"} },
{ sheet: 1, row: 4, col: 2, json: { data: "Super"} },
{ sheet: 1, row: 4, col: 3, json: { data: "Complete"} },
{ sheet: 1, row: 4, col: 4, json: { data: "2016-07-10"} },
{ sheet: 1, row: 4, col: 5, json: { data: "2016-07-12"} },
{ sheet: 1, row: 4, col: 7, json: { data: 1} },
{ sheet: 1, row: 5, col: 1, json: { data: "So Much To Get Done This Week"} },
{ sheet: 1, row: 5, col: 2, json: { data: "Super"} },
{ sheet: 1, row: 5, col: 3, json: { data: "Not started"} },
{ sheet: 1, row: 5, col: 4, json: { data: "2016-08-10"} },
{ sheet: 1, row: 5, col: 5, json: { data: "2016-08-12"} },
{ sheet: 1, row: 5, col: 7, json: { data: 0} },

{ sheet: 2, row: 0, col: 0, json: { height: 24, va: "middle"} },
{ sheet: 2, row: 0, col: 1, json: { data: "Priority", width: 150, ticon:"tag_orange", render: "lightGreenRender"} },
{ sheet: 2, row: 0, col: 2, json: { data: "Status", width: 150, ticon:"tag_red", render: "lightPurpleRender"} },
{ sheet: 2, row: 1, col: 1, json: { data: "Super"} },
{ sheet: 2, row: 2, col: 1, json: { data: "High"} },
{ sheet: 2, row: 3, col: 1, json: { data: "Normal"} },
{ sheet: 2, row: 4, col: 1, json: { data: "Low"} },
{ sheet: 2, row: 1, col: 2, json: { data: "Not started"} },
{ sheet: 2, row: 2, col: 2, json: { data: "In progress"} },
{ sheet: 2, row: 3, col: 2, json: { data: "Complete"} },
{ sheet: 2, row: 4, col: 2, json: { data: "Failed"} }
]
	};	
    
    var json = {
    		fileName: "Team task management",
    		sheets:[{id:1, name:"Projects", actived:true, color:"orange"},{id:2, name:"Tasks", actived:false, color:"green"}],
    		floatings: [
{ sheet:1, name:"colGroups", ftype:"colgroup", json: "[{level:1, span:[2,3]}, {level:1, span:[4,6]}]" }
    	    ],
    		cells:[
{ sheet: 1, row: 0, col: 0, json: { height: 25, va: "middle"} },
{ sheet: 1, row: 0, col: 1, json: { data: "Project", width: 220, ticon:"address", render:"orangeRender", beforeEdit: "_emptyCellCopyOthersFromPreRow_"} },
{ sheet: 1, row: 0, col: 2, json: { data: "Start date", width: 140, drop: "date", fm: "date", dfm: "F d, Y", ticon:"calendar"  } },
{ sheet: 1, row: 0, col: 3, json: { data: "Due date", width: 140, drop: "date", fm: "date", dfm: "F d, Y", ticon:"calendar"  } },
{ sheet: 1, row: 0, col: 4, json: { data: "Total tasks", width: 75, dcfg: "{dt:0}"} },
{ sheet: 1, row: 0, col: 5, json: { data: "Done tasks", width: 75, dcfg: "{dt:0}"} },
{ sheet: 1, row: 0, col: 6, json: { data: "Progress", dcfg: "{dt:12, format: '0%'}", ticon:"percent", color: "#FFA500" } },
{ sheet: 1, row: 0, col: 7, json: { width: 300, color: "#FFA500" } },
{ sheet: 1, row: 0, col: 8, json: { data: "Notes", width: 250, dcfg: "{dt:14}",  ticon:"textLong" } },

{ sheet: 1, row: 1, col: 1, json: { data: "Plan vacation"} },
{ sheet: 1, row: 1, col: 2, json: { data: "2016-05-01"} },
{ sheet: 1, row: 1, col: 3, json: { data: "2016-07-12"} },
{ sheet: 1, row: 1, col: 4, json: { data: "=countif('Tasks'!C:C,A1)", cal: true} },
{ sheet: 1, row: 1, col: 5, json: { data: "=countifs('Tasks'!E1:E100,'=YES','Tasks'!C1:C100,A1)", cal: true} },
{ sheet: 1, row: 1, col: 6, json: { data: "=IF(D1>0,E1/D1,0)", cal: true} },
{ sheet: 1, row: 1, col: 7, json: { data: "=rept('|', F1*100)", cal: true} },
{ sheet: 1, row: 1, col: 8, json: { data: "We are going to Belize" } },
{ sheet: 1, row: 2, col: 1, json: { data: "Train for Marathon"} },
{ sheet: 1, row: 2, col: 2, json: { data: "2016-07-15"} },
{ sheet: 1, row: 2, col: 3, json: { data: "2016-09-18"} },
{ sheet: 1, row: 2, col: 4, json: { data: "=countif('Tasks'!C:C,A2)", cal: true} },
{ sheet: 1, row: 2, col: 5, json: { data: "=countifs('Tasks'!E1:E100,'=YES','Tasks'!C1:C100,A2)", cal: true} },
{ sheet: 1, row: 2, col: 6, json: { data: "=IF(D2>0,E2/D2,0)", cal: true} },
{ sheet: 1, row: 2, col: 7, json: { data: "=rept('|', F2*100)", cal: true} },
{ sheet: 1, row: 2, col: 8, json: { data: "Goal: within 3 hours" } },
{ sheet: 1, row: 3, col: 1, json: { data: "Plan Evan birthday party"} },
{ sheet: 1, row: 3, col: 2, json: { data: "2016-09-20"} },
{ sheet: 1, row: 3, col: 3, json: { data: "2016-09-30"} },
{ sheet: 1, row: 3, col: 4, json: { data: "=countif('Tasks'!C:C,A3)", cal: true} },
{ sheet: 1, row: 3, col: 5, json: { data: "=countifs('Tasks'!E1:E100,'=YES','Tasks'!C1:C100,A3)", cal: true} },
{ sheet: 1, row: 3, col: 6, json: { data: "=IF(D3>0,E3/D3,0)", cal: true} },
{ sheet: 1, row: 3, col: 7, json: { data: "=rept('|', F3*100)", cal: true} },
{ sheet: 1, row: 4, col: 1, json: { data: "Kevin hockey lesson"} },
{ sheet: 1, row: 4, col: 2, json: { data: "2016-10-01"} },
{ sheet: 1, row: 4, col: 3, json: { data: "2016-12-30"} },
{ sheet: 1, row: 4, col: 4, json: { data: "=countif('Tasks'!C:C,A4)", cal: true} },
{ sheet: 1, row: 4, col: 5, json: { data: "=countifs('Tasks'!E1:E100,'=YES','Tasks'!C1:C100,A4)", cal: true} },
{ sheet: 1, row: 4, col: 6, json: { data: "=IF(D4>0,E4/D4,0)", cal: true} },
{ sheet: 1, row: 4, col: 7, json: { data: "=rept('|', F4*100)", cal: true} },

{ sheet: 2, row: 0, col: 0, json: { height: 25, va: "middle"} },
{ sheet: 2, row: 0, col: 1, json: { data: "Start date", width: 140, drop: "date", fm: "date", dfm: "F d, Y", ticon:"calendar"  } },
{ sheet: 2, row: 0, col: 2, json: { data: "End date", width: 140, drop: "date", fm: "date", dfm: "F d, Y", ticon:"calendar"  } },
{ sheet: 2, row: 0, col: 3, json: { data: "Project", width: 200, dcfg: "{dt:16, ignoreBlank: true, refList: '=Projects!$A:$A'}", ticon:"remoteList", render:"orangeRender"} },
{ sheet: 2, row: 0, col: 4, json: { data: "Tasks", width: 250, ticon:"key", render: "s_lightBlueRender"} },
{ sheet: 2, row: 0, col: 5, json: { data: "Done?", width: 80, drop: "list", dcfg: "{dt:13, list: [\"YES\",\"NO\"], ignoreBlank: true}", ticon:"dropdown", render:"blueRender"} },
{ sheet: 2, row: 0, col: 6, json: { data: "Attachments", width: 100, dcfg: "{dt:7}", ticon:"attach", render:"attachRender"}},
{ sheet: 2, row: 0, col: 7, json: { data: "Notes", width: 250, dcfg: "{dt:14}",  ticon:"textLong" } },

{ sheet: 2, row: 1, col: 1, json: { data: "2016-05-01"} },
{ sheet: 2, row: 1, col: 2, json: { data: "2016-05-03"} },
{ sheet: 2, row: 1, col: 3, json: { data: "Plan vacation"} },
{ sheet: 2, row: 1, col: 4, json: { data: "Choose between tropical & temperate"} },
{ sheet: 2, row: 1, col: 5, json: { data: "YES" } },
{ sheet: 2, row: 1, col: 7, json: { data: "Chose tropical. Belize here we come!" } },
{ sheet: 2, row: 2, col: 1, json: { data: "2016-05-04"} },
{ sheet: 2, row: 2, col: 2, json: { data: "2016-05-10"} },
{ sheet: 2, row: 2, col: 3, json: { data: "Plan vacation"} },
{ sheet: 2, row: 2, col: 4, json: { data: "Monitor spring air fare rates online"} },
{ sheet: 2, row: 2, col: 5, json: { data: "YES"  } },
{ sheet: 2, row: 3, col: 1, json: { data: "2016-05-11"} },
{ sheet: 2, row: 3, col: 3, json: { data: "Plan vacation"} },
{ sheet: 2, row: 3, col: 4, json: { data: "Study the history and culture of Belize"} },
{ sheet: 2, row: 4, col: 1, json: { data: "2016-05-11"} },
{ sheet: 2, row: 4, col: 2, json: { data: "2016-05-20"} },
{ sheet: 2, row: 4, col: 3, json: { data: "Plan vacation"} },
{ sheet: 2, row: 4, col: 4, json: { data: "Study resort reviews"} },
{ sheet: 2, row: 4, col: 5, json: { data: "YES"  } },
{ sheet: 2, row: 4, col: 7, json: { data: "Consider the rush option..." } },
{ sheet: 2, row: 5, col: 1, json: { data: "2016-07-15"} },
{ sheet: 2, row: 5, col: 2, json: { data: "2016-07-31"} },
{ sheet: 2, row: 5, col: 3, json: { data: "Train for Marathon"} },
{ sheet: 2, row: 5, col: 4, json: { data: "10 miles a day"} },
{ sheet: 2, row: 5, col: 5, json: { data: "YES"  } },
{ sheet: 2, row: 5, col: 7, json: { data: "Race is on September 18" } },
{ sheet: 2, row: 6, col: 1, json: { data: "2016-08-01"} },
{ sheet: 2, row: 6, col: 2, json: { data: "2016-08-30"} },
{ sheet: 2, row: 6, col: 3, json: { data: "Train for Marathon"} },
{ sheet: 2, row: 6, col: 4, json: { data: "12 miles a day"} },
{ sheet: 2, row: 7, col: 1, json: { data: "2016-09-01"} },
{ sheet: 2, row: 7, col: 2, json: { data: "2016-09-15"} },
{ sheet: 2, row: 7, col: 3, json: { data: "Train for Marathon"} },
{ sheet: 2, row: 7, col: 4, json: { data: "15 miles a day"} },
{ sheet: 2, row: 8, col: 1, json: { data: "2016-09-16"} },
{ sheet: 2, row: 8, col: 3, json: { data: "Train for Marathon"} },
{ sheet: 2, row: 8, col: 4, json: { data: "Travel to marathon"} },
{ sheet: 2, row: 9, col: 1, json: { data: "2016-09-20"} },
{ sheet: 2, row: 9, col: 2, json: { data: "2016-09-21"} },
{ sheet: 2, row: 9, col: 3, json: { data: "Plan Evan birthday party"} },
{ sheet: 2, row: 9, col: 4, json: { data: "Send party invitations"} },
{ sheet: 2, row: 9, col: 5, json: { data: "YES"  } },
{ sheet: 2, row: 10, col: 1, json: { data: "2016-09-30"} },
{ sheet: 2, row: 10, col: 2, json: { data: "2016-09-30"} },
{ sheet: 2, row: 10, col: 3, json: { data: "Plan Evan birthday party"} },
{ sheet: 2, row: 10, col: 4, json: { data: "Party day!"} },
{ sheet: 2, row: 11, col: 1, json: { data: "2016-10-01"} },
{ sheet: 2, row: 11, col: 2, json: { data: "2016-10-30"} },
{ sheet: 2, row: 11, col: 3, json: { data: "Kevin hockey lesson"} },
{ sheet: 2, row: 11, col: 4, json: { data: "First round practise"} },

    		]
	};	
	    
    **/
    var json = {
		fileName: "Employee Directory",
		sheets:[{id:1, name:"Main view", actived:true, color:"orange"}],
		floatings: [
	        { sheet:1, name:"colGroups", ftype:"colgroup", json: "[{level:1, span:[2,3]}, {level:1, span:[4,6]}]" },
	    ],
		cells:[
		    { sheet: 1, row: 0, col: 0, json: { height: 25, va: "middle"} },
		    { sheet: 1, row: 0, col: 1, json: { data: "ID", width: 50, dcfg: "{dt:0, io:true, min:0, max:10000, op:0, ignoreBlank: true, titleIcon: \"number\"}", ticon:"number" } },
			{ sheet: 1, row: 0, col: 2, json: { data: "Name", width: 100, ticon:"profile"} },
			{ sheet: 1, row: 0, col: 3, json: { data: "Dept.(Remote)", width: 130, drop: "list", dcfg: "{dt:15, url: \"fakeData/dropdownList\", titleIcon:  \"remoteList\"}", ticon:"remoteList" } },
			{ sheet: 1, row: 0, col: 4, json: { data: "Email", width: 110, dcfg: "{dt:9, ignoreBlank: true}", ticon:"email" } },
			{ sheet: 1, row: 0, col: 5, json: { data: "Phone", width: 100, dcfg: "{dt:8, ignoreBlank: true}", ticon:"phone" } },
			{ sheet: 1, row: 0, col: 6, json: { data: "Gender", width: 80, drop: "list", dcfg: "{dt:13, list: [\"Male\",\"Female\"], ignoreBlank: true}", ticon:"dropdown" } },
			{ sheet: 1, row: 0, col: 7, json: { data: "Birth date", width: 120, drop: "date", fm: "date", dfm: "F d, Y", ticon:"calendar"  } },			
			{ sheet: 1, row: 0, col: 8, json: { data: "Contact picker", width: 170, ticon:"contact", beforeEdit: "_beforeeditcell_" } },
			{ sheet: 1, row: 0, col: 9, json: { data: "Images", width: 130, dcfg: "{dt:7}", ticon:"image" } },
			{ sheet: 1, row: 0, col: 10, json: { data: "Manager?", width: 100, it: "checkbox", itchk: false, ta: "center", ticon:"checkbox" } },			
			{ sheet: 1, row: 0, col: 11, json: { data: "Salary", dcfg: "{dt:11, format: \"money|$|2|none|usd|true\"}",  ticon:"money_dollar" } },
			{ sheet: 1, row: 0, col: 12, json: { data: "Percent", dcfg: "{dt:12, format: \"0.00%\"}",  ticon:"percent" } },
			{ sheet: 1, row: 0, col: 13, json: { data: "Notes", dcfg: "{dt:14, titleIcon: \"textLong\"}",  ticon:"textLong" } },
			
			{ sheet: 1, row: 1, col: 1, json: { data: 1 } },
			{ sheet: 1, row: 1, col: 2, json: { data: 'Jerry Marc' } },
			{ sheet: 1, row: 1, col: 3, json: { render:'dropRender', data: 'HR Dept', dropId: 1} },
			{ sheet: 1, row: 1, col: 4, json: { data: 'john.marc@abc.com'} },
			{ sheet: 1, row: 1, col: 5, json: { data: '1 (613) 456-7654'} },
			{ sheet: 1, row: 1, col: 6, json: { data: 'Female'} },
			{ sheet: 1, row: 1, col: 7, json: { data: '1982-01-15', fm: "date", dfm: "F d, Y" } },
			{ sheet: 1, row: 1, col: 8, json: { render:'contactRender', data: "Eva Mat, John Marc", itms: '[{name: "Eva Mat", email: "eva@gmail.com", id: 8}, {name: "John Marc", email: "john@abc.com", id: 9}]' } },
			{ sheet: 1, row: 1, col: 9, json: { render:'attachRender', itms: '[{aid: "rT7KfpHA8cI_", url: "sheetAttach/downloadFile?attachId=rT7KfpHA8cI_", type: "img", name: "blue.jpg"},{aid: "2ZisVQ1-*Lo_", url: "sheetAttach/downloadFile?attachId=2ZisVQ1-*Lo_", type: "img", name: "green.jpg"}]' } },
			{ sheet: 1, row: 1, col: 11, json: { data: 82334.5678 } },
			{ sheet: 1, row: 1, col: 12, json: { data: 0.96 } },
			{ sheet: 1, row: 1, col: 13, json: { data: 'This is notes, it is a long text. Double click to edit it.' } },
			
			{ sheet: 1, row: 2, col: 1, json: { data: 2 } },
			{ sheet: 1, row: 2, col: 2, json: { data: 'Dave Smith' } },
			{ sheet: 1, row: 2, col: 3, json: { render:'dropRender', data: 'Software Dept', dropId: 2} },
			{ sheet: 1, row: 2, col: 4, json: { data: 'dave.smith@abc.com'} },
			{ sheet: 1, row: 2, col: 5, json: { data: '1 (613) 231-7654'} },
			{ sheet: 1, row: 2, col: 6, json: { data: 'Male'} },
			{ sheet: 1, row: 2, col: 7, json: { data: '1980-01-15', fm: "date", dfm: "F d, Y" } },
			{ sheet: 1, row: 2, col: 8, json: { render:'contactRender', data: "Christina Angela, Marina Chris", itms: '[{name: "Christina Angela", email: "christina@gmail.com", id: 4}, {name: "Marina Chris", email: "marina@abc.com", id: 6}]' } },
			{ sheet: 1, row: 2, col: 9, json: { render:'attachRender', itms: '[{aid: "CIBHu3ffG8Q_", url: "sheetAttach/downloadFile?attachId=CIBHu3ffG8Q_", type: "img", name: "admin.png"},{aid: "VcrhEYAyrzA_", url: "sheetAttach/downloadFile?attachId=VcrhEYAyrzA_", type: "img", name: "asset.png"}]' } },
			{ sheet: 1, row: 2, col: 11, json: { data: 81234.5678 } },
			{ sheet: 1, row: 2, col: 12, json: { data: 0.95 } },
			{ sheet: 1, row: 2, col: 13, json: { data: 'This is notes, it is a long text. Double click to edit it.' } },
			
			{ sheet: 1, row: 3, col: 1, json: { data: 3 } },
			{ sheet: 1, row: 3, col: 2, json: { data: 'Kevin Featherstone' } },
			{ sheet: 1, row: 3, col: 3, json: { render:'dropRender', data: 'Software Dept', dropId: 2} },
			{ sheet: 1, row: 3, col: 4, json: { data: 'kevin@abc.com'} },
			{ sheet: 1, row: 3, col: 5, json: { data: '1 (613) 232-7654'} },
			{ sheet: 1, row: 3, col: 6, json: { data: 'Male'} },
			{ sheet: 1, row: 3, col: 7, json: { data: '1990-01-15', fm: "date", dfm: "F d, Y" } },
			{ sheet: 1, row: 3, col: 8, json: { render:'contactRender', data: "Christina Angela, Marina Chris", itms: '[{name: "Christina Angela", email: "christina@gmail.com", id: 4}, {name: "Marina Chris", email: "marina@abc.com", id: 6}]' } },
			{ sheet: 1, row: 3, col: 9, json: { render:'attachRender', itms: '[{aid: "CIBHu3ffG8Q_", url: "sheetAttach/downloadFile?attachId=CIBHu3ffG8Q_", type: "img", name: "admin.png"},{aid: "VcrhEYAyrzA_", url: "sheetAttach/downloadFile?attachId=VcrhEYAyrzA_", type: "img", name: "asset.png"}]' } },
			{ sheet: 1, row: 3, col: 11, json: { data: 81934.5678 } },
			{ sheet: 1, row: 3, col: 12, json: { data: 0.98 } },
			{ sheet: 1, row: 3, col: 13, json: { data: 'This is notes, it is a long text. Double click to edit it.' } }
		]
	};	

    
	SHEET_API.loadData(SHEET_API_HD, json, null, this);
	SHEET_API.setFocus(SHEET_API_HD, 2, 1); 
   
	// add event listener - this shows the code to add customer function 
	var sheet = SHEET_API_HD.sheet;
	var editor = sheet.getEditor();
	editor.on('quit', function(editor, sheetId, row, col) {		
		if (col === 1) {
			// this is the method to query customer existing backend and auto fill data
			//var employeeId = SHEET_API.getCellValue(SHEET_API_HD, sheetId, row, col).data;
			//if (employeeId) AUTO_FILL_CUSTOMER_DATA_BY_EMPLOYEEID(employeeId, sheetId, row, col);
			SHEET_API.copyPasteRange(SHEET_API_HD, [[sheetId, row-1, 6, row-1, 7]], [[sheetId, row, 6, row, 7]]);
		}			
	}, this); 
	
	/**
     * This part just add your defined function ...can be removed if need 
     * You can add your defined event, and your defined custom code
     */
    // =============================Start you defined event listener ==============================
    CUSTOMER_DEFINED_CELL_EDITOR_FN(SHEET_API_HD.sheet);        
	// ============================End your defined event listener ==================================
	
	// add cell on select event ...
	/**
	var sm = sheet.getSelectionModel();
	sm.on('selectionchange', function(startPos, endPos, region, sm) {
	    if (startPos.row == endPos.row && startPos.col == endPos.col && startPos.col == 8) {
	    	this.customEditor = Ext.create('customer.CellEditor', {
	    		sheetId: region.sheetId,
	    		row: startPos.row,
	    		col: startPos.col
	    	});
	    	this.customEditor.popup();
	    }
	}, this);
	**/
	
	
});
	
