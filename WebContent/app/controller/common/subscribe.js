Ext.QuickTips.init();
Ext.define('erp.controller.common.subscribe', {
	extend : 'Ext.app.Controller',
	requires : ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
	views : ['common.subs.subscribeView', 'common.subs.nosubscribegrid',
			'common.subs.subscribedgrid'],
	init : function() {
		var me = this;
		this.BaseUtil = Ext.create('erp.util.BaseUtil');
		this.FormUtil = Ext.create('erp.util.FormUtil');
		this.GridUtil = Ext.create('erp.util.GridUtil');		
		this.control({
			
			'erpnosubscribeGridPanel':{
				beforeshow:function(t,o){					
					t.store.load();	
				}
			},
			'erpsubscribedGridPanel':{
				beforeshow:function(t,o){
					t.store.load();					
				}
			}
		});	
	}	
});
function showWindow(insId){ 
	var me=this;
	var url='common/charts/mobilePreview.action?id='+insId;
	if (Ext.getCmp('chwin')) {
		Ext.getCmp('chwin').insId=insId;
		Ext.getCmp('chwin').body.update('<iframe id="iframech" src="'+basePath+url+'" height="100%" width="100%" frameborder="0" scrolling="auto"  ></iframe>');
		}
	else {
	var chwin = new Ext.window.Window({
	   id : 'chwin',
	   title: '预览',
	   height: "100%",
	   width: "40%",
	   insId:insId,	   
	   resizable:false,
	   modal:true,
	   buttonAlign : 'center',
	   layout : 'anchor',
	   items: [{
		   tag : 'iframe',
		   frame : true,
		   anchor : '100% 100%',
		   layout : 'fit',
		   html : '<iframe id="iframech" src="'+basePath+url+'" height="100%" width="100%" frameborder="0" scrolling="auto"  ></iframe>'
	   }],	
	   buttons : [{
		   text : '关  闭',
		   iconCls: 'x-button-icon-close',
		   cls: 'x-btn-gray',
		   handler : function(){
			   Ext.getCmp('chwin').close();				   				   				 
		   }
	   
	   }]
   });  
	chwin.show();}};
