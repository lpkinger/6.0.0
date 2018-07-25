Ext.QuickTips.init();
Ext.define('erp.controller.oa.persontask.myDocument.ReceivedDocument', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'oa.persontask.myDocument.Viewport','common.datalist.GridPanel','common.datalist.Toolbar',
     		'core.trigger.DbfindTrigger','core.form.ConDateField','core.form.WordSizeField','oa.mail.MailPaging'
     	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpDatalistGridPanel': {
    			afterrender: function(grid){
    				grid.onGridItemClick = function(){//改为点击button进入详细界面
    					me.onGridItemClick(grid.selModel.lastSelected);
    				};
    			}
    		},
    		'button[id=sod]': {
    			click: function(){
//    				var grid = Ext.getCmp('grid');
    				var orid = 0;
    				Ext.Ajax.request({//拿到grid的columns
    		        	url : basePath + "hr/employee/getHrOrg.action",
    		        	params: {
    		        		em_id: em_uu
    		        	},
    		        	method : 'post',
    		        	async: false,
    		        	callback : function(options, success, response){
    		        		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
    		        		console.log(response);
    		        		var res = new Ext.decode(response.responseText);
    		        		if(res.exceptionInfo){
    		        			showError(res.exceptionInfo);
    		        		}
    		        		if(res.hrOrg){
    		        			orid = res.hrOrg.or_id;
    		        		}
    		        	}
    				});
    				var condition = 'urlcondition=sod_zs_organ_id=' + orid;// + " OR sod_zs_emp_id like '%" + em_uu + "%'";
    				var path = window.location.href.toString().split('?');
    				window.location.href = path[0] + '?whoami=SendOfficialDocument!See&' + condition;
    			}
    		},
    		'button[id=all]':{
    			afterrender: function(btn){
    				var who = getUrlParam('whoami');
    				if(contains(who, 'Instruction', true)){
    		    		btn.setVisible(false);
    				} else {
    					btn.setVisible(true);
    				}
    			},
    			click: function(){
    				var grid = Ext.getCmp('grid');
    				var orid = 0;
    				Ext.Ajax.request({//拿到grid的columns
    		        	url : basePath + "hr/employee/getHrOrg.action",
    		        	params: {
    		        		em_id: em_uu
    		        	},
    		        	method : 'post',
    		        	async: false,
    		        	callback : function(options, success, response){
    		        		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
    		        		console.log(response);
    		        		var res = new Ext.decode(response.responseText);
    		        		if(res.exceptionInfo){
    		        			showError(res.exceptionInfo);
    		        		}
    		        		if(res.hrOrg){
    		        			orid = res.hrOrg.or_id;
    		        		}
    		        	}
    				});
    				grid.getCount('SendOfficialDocument!See', '1=1');
    				grid.filterCondition = 'sod_zs_organ_id=' + orid + " OR sod_zs_emp_id like '%" + em_uu + "%'";
    			}
    		},
    		'button[id=in]': {
    			click: function(){
//    				var grid = Ext.getCmp('grid');
    				var condition = "urlcondition=in_leader_id='" + em_code + "'";
    				var path = window.location.href.toString().split('?');
    				window.location.href = path[0] + '?whoami=Instruction!See&' + condition;
    			}
    		}
    	});
    },
    onGridItemClick: function(record){//grid行选择
    	console.log(record);
    	var me = this;
    	var who = getUrlParam('whoami');
    	var path = '';
    	var id = 0;
    	var title = '';
    	if(contains(who, 'Send', true)){
    		id = record.data.sod_id;
    		title = '发文:' + record.data.sod_title;
			path = 'jsps/oa/officialDocument/sendODManagement/sodDetail.jsp';
		} else if(contains(who, 'Instruction', true)){
    		id = record.data.in_id;
    		title = '内部请示:' + record.data.in_title;
			path = 'jsps/oa/officialDocument/instruction/instructionDetail.jsp';
		}
    	var panel = Ext.getCmp(who + id); 
    	var main = parent.Ext.getCmp("content-panel");
    	if(!panel){ 
	    	panel = { 
	    			title : title,
	    			tag : 'iframe',
	    			tabConfig:{tooltip: title},
	    			frame : true,
	    			border : false,
	    			layout : 'fit',
	    			iconCls : 'x-tree-icon-tab-tab1',
	    			html : '<iframe id="iframe_' + who + id + '" src="' + basePath + path + "?flag=query&id=" + id + '" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>',
	    			closable : true,
	    			listeners : {
	    				close : function(){
	    			    	main.setActiveTab(main.getActiveTab().id); 
	    				}
	    			} 
	    	};
	    	me.FormUtil.openTab(panel, who + id); 
    	}else{ 
	    	main.setActiveTab(panel); 
    	} 
    }

});