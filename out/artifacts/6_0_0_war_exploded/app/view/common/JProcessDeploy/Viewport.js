Ext.define('erp.view.common.JProcessDeploy.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	BaseUtil: Ext.create('erp.util.BaseUtil'),// 获得 参数 。
	initComponent : function(){ 
		var me = this; 
		var formPanel = Ext.create("erp.view.core.form.Panel",{
    		anchor: '100% 20%' ,
    		id:'form' ,
    		dumpable: true,
    		_noc:1
    		/*hidden:'true'*/
    	});
		Ext.apply(me, { 
			items: [{ 
				id:'JProcessViewport', 
				layout: 'anchor', 
				anchor: '100% 100%' ,
				items: [formPanel]
			}] 
		}); 
		me.callParent(arguments); 
	},
	listeners:{
		afterrender:function(){
			formCondition = this.BaseUtil.getUrlParam('formCondition');//从url解析参数
	    	formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
	    	var strArr = formCondition.split("=");
	    	/*var xmlInfo='';*/
	    	/*Ext.Ajax.request({
			    url: basePath + 'common/getJProcessDeployInfo.action',
			    params: {
			        jdId: strArr[1]
			    },
			    success: function(response){
			    	var text = new Ext.decode(response.responseText);
			        console.log(text);
			        xmlInfo = text.xmlInfo;
			        console.log(xmlInfo);
			    }
			});*/
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
	    			height:window.innerHeight*0.8,
	    			iconCls : 'x-tree-icon-tab-tab',
	    			html : '<iframe id="iframe_maindetail_" src="../../workfloweditor/workfloweditor2.jsp?jdId='+strArr[1]+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
	    			};
			
			Ext.getCmp("JProcessViewport").add(panel);
			}
		}
});