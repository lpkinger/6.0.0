Ext.QuickTips.init();
Ext.define('erp.controller.oa.persontask.workPlan.Query', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.persontask.workPlan.Query','core.form.Panel',
    		'common.datalist.GridPanel','common.datalist.Toolbar','core.form.ConDateField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.DetailTextField','core.form.FileField'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpDatalistGridPanel': {
//    			afterrender: function(grid){
//    				grid.onGridItemClick = function(){//改为点击button进入详细界面
//    					me.onGridItemClick(grid.selModel.lastSelected);
//    				};
//    			}
    			itemclick: this.onGridItemClick
    		},
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onGridItemClick: function(selModel, record){//grid行选择
    	console.log(record);
    	var me = this;
    	var path = 'jsps/oa/persontask/workPlan/register.jsp';
    	var id = record.data.wp_id;
    	var title = '工作计划查看';
    	var last = me.getLast(record.data.wp_title);
    	var panel = Ext.getCmp('workplan' + id); 
    	var main = parent.Ext.getCmp("content-panel");
    	if(!panel){ 
	    	panel = { 
	    			title : title,
	    			tag : 'iframe',
	    			tabConfig:{tooltip: record.data.wp_title},
	    			frame : true,
	    			border : false,
	    			layout : 'fit',
	    			iconCls : 'x-tree-icon-tab-tab1',
	    			html : '<iframe id="iframe_' + id + '" src="' + basePath + path + "?nextworkplan=wp_idIS" + id + (last == 0 ? '':'&lastworkplan=wp_idIS' + last ) + '" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>',
	    			closable : true,
	    			listeners : {
	    				close : function(){
	    			    	main.setActiveTab(main.getActiveTab().id); 
	    				}
	    			} 
	    	};
	    	me.FormUtil.openTab(panel, 'workplan' + id); 
    	}else{ 
	    	main.setActiveTab(panel); 
    	} 
    },
    getLast: function(title){
    	var lt = '';
    	var f = title.split(/\d{4}/);
    	console.log(f);
    	if(contains(title, '年度', true)){
    		lt = f[0] + (title.substr(em_name.length+1, 4)-1) + f[1];
    	} else if(contains(title, '季度', true)){
    		var fv = f[1].split(/\d{1}/);
    		if(contains(f[1], '1', true)){
    			lt = f[0] + (title.substr(em_name.length+1, 4)-1) + fv[0] + '4' + fv[1];
    		} else {
    			lt = f[0] + (title.substr(em_name.length+1, 4)) + fv[0] + (title.charAt(title.indexOf('第') + 1)-1) + fv[1];
    		}
    	} else if(contains(title, '月', true)){
    		var fu = f[1].split(/\d{2}/);
    		if(contains(f[1], '01', true)){
    			lt = f[0] + (title.substr(em_name.length+1, 4)-1) + fu[0] + '12' + fu[1];
    		} else {
    			var moo = title.substr(title.indexOf('年')+1, 2);
    			lt = f[0] + (title.substr(em_name.length+1, 4)) + fu[0] + (moo-1>9 ? moo-1 : '0'+(moo-1)) + fu[1];
    		}
    	}
    	var lp = 0;
    	Ext.Ajax.request({
			url : basePath + 'oa/persontask/workPlan/queryWorkPlan.action',
			method : 'post',
			params:{
				title: lt
			},
			async: false,
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				if(rs.success && rs.workplan){
					lp = rs.workplan.wp_id;
				}
			}
    	});
    	return lp;
    }
});