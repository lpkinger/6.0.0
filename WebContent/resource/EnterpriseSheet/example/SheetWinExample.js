/**
 * Enterprise Spreadsheet Solution
 * Copyright (c) FeyaSoft Inc 2015. All right reserved.
 * http://www.enterpriseSheet.com
 */
Ext.onReady(function(){
	
    SCONFIG.setupDir('');
    
    SHEET_API = Ext.create('EnterpriseSheet.api.SheetAPI', {
	    openFileByOnlyLoadDataFlag: true
	});
           
    var win;
    POP_SHEET_WIN_1 = function(){
	    
	    SHEET_API_HD_1 = SHEET_API.createSheetApp({
		   withoutTitlebar: false,
	       withoutSheetbar: false,
	       withoutToolbar: false,
	       withoutContentbar: false,
	       withoutSidebar: false   
		});
    
        if(!win){
            win = Ext.create('Ext.window.Window', {
                resizable : true,
               //  modal: true,
                buttonAlign : "right",
                closable : true,
                closeAction : 'hide',
                width : 1000,
                height: 600,
                layout : 'fit',
                items: [SHEET_API_HD_1.appCt],
                buttons: [{
                    text: 'Close',
                    handler: function(){
                        win.hide();
                    }
                },{
                    text: 'Submit',
                    handler: function(){
                        var json = SHEET_API.getJsonData(SHEET_API_HD_1);
                        alert(Ext.encode(json));
                    }
                }]
            });
        }
        win.show();

        var b2CellValue = document.getElementById("cellValue").value;
        var json = {
            fileName:"Instance 1",
            sheets:[{id:1,name:"instance 1 - tab",actived:true,color:"orange"}],
            cells:[{i:1,x:2,y:2,j:"{data: \"" + b2CellValue + "\"}"}]
        };
        
        SHEET_API.loadData(SHEET_API_HD_1, json);
    };
    
    var win2;
    POP_SHEET_WIN_2 = function(){
	    
	    SHEET_API_HD_2 = SHEET_API.createSheetApp({
		   withoutTitlebar: false,
	       withoutSheetbar: false,
	       withoutToolbar: false,
	       withoutContentbar: false,
	       withoutSidebar: false   
		});
    	
        if(!win2){
            win2 = Ext.create('Ext.window.Window', {
                resizable : true,
               //  modal: true,
                buttonAlign : "right",
                closable : true,
                closeAction : 'hide',
                width : 1000,
                height: 600,
                layout : 'fit',
                items: [SHEET_API_HD_2.appCt],
                buttons: [{
                    text: 'Close',
                    handler: function(){
                        win2.hide();
                    }
                }]
            });
        }
        win2.show();
        
        var b2CellValue = document.getElementById("cellValue").value;
        var json = {
            fileName:"Instance 2",
            sheets:[{id:1,name:"instance 2 - tab",actived:true,color:"orange"}],
            cells:[{i:1,x:2,y:2,j:"{data: \"" + b2CellValue + "\"}"}]
        };
        
        SHEET_API.loadData(SHEET_API_HD_2, json);
    }
});