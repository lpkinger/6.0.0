Ext.QuickTips.init();
Ext.define('erp.controller.common.AutoAudit', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'common.JProcess.JProcessAutoAudit.Viewport',
    		'common.JProcess.JProcessAutoAudit.NewrRequireApply',
    		'common.JProcess.JProcessAutoAudit.OtherRules',
    		'common.JProcess.JProcessAutoAudit.ChangeRules',	
   			 'core.form.Panel','core.grid.Panel'	
    	],
    init:function(){
    	var me = this;
    	this.control({
    		'#toolbar':{
    			afterrender:function(t){
    				var flowbody=parent.Ext.getCmp("flowbody");
    				t.currentnode=flowbody.currentnode;
					t.processtitle= flowbody.processtitle;
					t.caller=flowbody.caller;
    			}
    		},

    		'#rulegrid':{
    			afterrender:function(t){
    				me.getJprocessRuleAndApply(t);
    			}
    		},
    		'#otherrulesgrid':{
    			cellclick:function(t,td,cellIndex,record,tr,rowIndex){
    				if(cellIndex==0){
    					var toolbar=Ext.getCmp('toolbar');
    					toolbar.caller=record.data.JD_CALLER;
						toolbar.currentnode=record.data.JT_NAME;
						toolbar.processtitle=record.data.JD_PROCESSDEFINITIONID;
						document.getElementById('protitle').innerHTML = record.data.JD_PROCESSDEFINITIONID;
						document.getElementById('curnode').innerHTML = record.data.JT_NAME;
    					var rulegrid=Ext.getCmp('rulegrid');
    					rulegrid.showBtn=true;
    					me.getJprocessRuleAndApply(rulegrid);
    				}
    				
    			}
    		},
    		'#historygrid':{
    			afterrender:function(t){
    				me.getRulesApplyHistory(t);
    			}
    		}
    	});
    } ,
     getRulesApplyHistory:function(t){
     	console.log(t.caller);
     	console.log(t.nodename);
     	
    	var nodename=t.nodename;
		var caller=t.caller;
    	Ext.Ajax.request({
			url: basePath + 'common/getRulesApplyHistory.action',
			params: {
				caller:caller,
				nodename:nodename,
				_noc: 1
			},
			callback: function(options, success, response) {
				var text = Ext.decode(response.responseText);
				Ext.getCmp('historygrid').store.loadData(text);
			}
		}); 	
    },
    getJprocessRuleAndApply:function(t){
    	var flowbody=parent.Ext.getCmp("flowbody");
    	var toolbar=Ext.getCmp('toolbar');
		var caller=toolbar.caller;
		var currentnode=toolbar.currentnode;
    	Ext.Ajax.request({
			url: basePath + 'common/getJprocessRuleAndApply.action',
			params: {
				caller:caller,
				currentnode:currentnode,
				_noc: 1
			},
			callback: function(options, success, response) {
				var text = Ext.decode(response.responseText);
				var flag=false;
				if(text.length>0){
					Ext.getCmp("newapplyfieldset").setExpanded(false);
					Ext.getCmp("chooserulesfieldset").setExpanded(true);
				}else{
					Ext.getCmp("chooserulesfieldset").setExpanded(false);
					Ext.getCmp("newapplyfieldset").setExpanded(true);
				}
				Ext.each(text,function(i){
					if(i.RA_STATUSCODE=='COMMITED'){
						t.showBtn=false;
						return false;
					}
				});
				t.store.loadData(text);
			}
		});
    },
    changeRules:function(select){
 		var id=select.RU_ID;
 		//下面这两个字段要从其他页面获取值，现在写死。
 		var flowbody=parent.Ext.getCmp('flowbody');
		var nodename=flowbody.currentnode;
		var processname= flowbody.processtitle;
    	Ext.Ajax.request({
			url: basePath + 'common/changeRules.action',
			params: {
				id:id,
				nodename:nodename,
				processname:processname,
				_noc: 1
			},
			callback: function(options, success, response) {
				var response = Ext.decode(response.responseText);
				if(response.success){
					Ext.getCmp("newrequireapplybtn").disable();
					Ext.getCmp("changerulesbtn").disable();
					alert("规则更改申请成功！");
				}				
			}
		});
    },
});