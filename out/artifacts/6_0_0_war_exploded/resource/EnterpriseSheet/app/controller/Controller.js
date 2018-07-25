/**
 * ClassName 图书控制器
 */
Ext.define("EnterpriseSheetApp.controller.Controller",{
	extend:'Ext.app.Controller',
	SHEET_API:Ext.create('EnterpriseSheet.api.SheetAPI'),
	init: function(){
				
		this.control({
			'#view':{
				afterrender:function(t){
					var hd = Ext.getCmp('view').SHEET_API_HD;
					
					console.log(hd);
										
				}
				
			}


		})
	},
	views:[
		'EnterpriseSheetApp.view.Viewport'
	],
	stores:[

	],
	models:[

	]
});