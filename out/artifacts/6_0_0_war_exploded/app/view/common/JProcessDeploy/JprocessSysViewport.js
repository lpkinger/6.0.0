Ext.define('erp.view.common.JProcessDeploy.JprocessSysViewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	BaseUtil: Ext.create('erp.util.BaseUtil'),// 获得 参数 。
	initComponent : function(){ 
		var me = this; 
		var formPanel = Ext.create("erp.view.core.form.Panel",{
    		anchor: '100% 10%' ,
    		id:'form' ,
    		_noc:1
    		/*hidden:'true'*/
    	});
		Ext.apply(me, { 
			items: [{ 
				id:'JProcessViewport', 
				layout: 'anchor', 
				items: [formPanel]
			}] 
		}); 
		me.callParent(arguments); 
	},
	listeners:{
		afterrender:function(){
			formCondition = this.BaseUtil.getUrlParam('formCondition');//从url解析参数
			panel = { 
	    			/*title : '单据信息',*/
	    			tag : 'iframe',
	    			style:{
			    		  background:'transparent',
		                  border:'none'
			    	  },
	    			/*tabConfig:{tooltip:main.getActiveTab().tabConfig.tooltip+'('+keyField + "=" + value+')'},*/
	    			frame : true,
	    			border : false,
	    			/*bodyBorder:false,*/
	    			layout : 'fit',
	    			height:window.innerHeight*0.9,
	    			iconCls : 'x-tree-icon-tab-tab',
	    			html : '<iframe id="iframe_maindetail_" src="../../workfloweditor/flownavigation.jsp?formCondition='+formCondition+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
	    			};
			Ext.getCmp("JProcessViewport").add(panel);
			}
		}
});