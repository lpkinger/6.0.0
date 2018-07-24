Ext.define('erp.view.oa.persontask.workPlan.WorkPlanForm2',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpWorkPlanFormPanel2',
	id: 'form', 
	BaseUtil: Ext.create('erp.util.BaseUtil'),
    region: 'center',
    frame : true,
    autoScroll:true,
    fieldDefaults: {
        labelWidth: 80,
        cls: 'form-field-allowBlank'
    },
    layout: {
        type: 'column',
        align: 'stretch'  // Child items are stretched to full width子面板高度充满父容器 
    },
    items: [{
        
    }],
//    tbar: [{
//    	id: 'save',
//    	text: '保存',
//    	iconCls: 'x-button-icon-save',
//    	cls: 'x-btn-gray',
////    	handler: function(){
////    		alert(Ext.getCmp('wp_summary').value);
////    	}
//    },{
//    	id: 'over',
//    	text: '结束',
//    	iconCls: 'x-button-icon-save',
//    	cls: 'x-btn-gray'
//    }],
	initComponent : function(){
//		var nextworkplan = getUrlParam('nextworkplan');
//		var lastworkplan = getUrlParam('lastworkplan');
		this.callParent(arguments);
//		this.getLast(lastworkplan);
//		this.getNext(nextworkplan);
//		this.getRODDetail(getUrlParam('id'));
	},
	getNext: function(nextworkplan){
		if(nextworkplan!=null && nextworkplan!=''){
			var id = nextworkplan.split('IS')[1];
			Ext.Ajax.request({
		   		url : basePath + 'oa/persontask/workPlan/getWorkPlan.action',
		   		params : {
		   			id: id
		   		},
		   		method : 'post',
		   		async: false,
		   		callback : function(options,success,response){
//		   			me.getActiveTab().setLoading(false);
		   			var res = new Ext.decode(response.responseText);
	    			if(res.success){
	    				Ext.getCmp('wp_id').setValue(res.workplan.wp_id);
	    				Ext.getCmp('wp_type').setValue(res.workplan.wp_type);
	    				Ext.getCmp('wp_typeid').setValue(res.workplan.wp_typeid);
	    				Ext.getCmp('wp_emp').setValue(res.workplan.wp_emp);
	    				Ext.getCmp('wp_empid').setValue(res.workplan.wp_empid);
	    				Ext.getCmp('wp_summary').setValue(res.workplan.wp_summary);
	    				Ext.getCmp('wp_status').setValue(res.workplan.wp_status);
	    				Ext.getCmp('wp_statuscode').setValue(res.workplan.wp_statuscode);
	    				Ext.getCmp('wp_sumattachs').setValue(res.workplan.wp_sumattachs);
	    				Ext.getCmp('wp_planattachs').setValue(res.workplan.wp_planattachs);
	    				if(res.workplan.wp_updatetime){
	    					Ext.getCmp('wp_committime').setValue(Ext.util.Format.date(new Date(res.workplan.wp_committime),"Y-m-d H:i:s"));	
	    				}	    				
	    				if(res.workplan.wp_updatetime){
	    					Ext.getCmp('wp_updatetime').setValue(Ext.util.Format.date(new Date(res.workplan.wp_updatetime),"Y-m-d H:i:s"));	    					
	    				}
	    				Ext.getCmp('wp_time').setValue(res.workplan.wp_time);
	    				Ext.getCmp('wp_title').setValue(res.workplan.wp_title);
	    				var values = '';
	    				for(var i=0; i<res.workplandetaillist.length; i++){
	    					if(i==res.workplandetaillist.length-1){
	    						values += res.workplandetaillist[i].wpd_plan;	    						
	    					} else {
	    						values += res.workplandetaillist[i].wpd_plan + '==###==';	 
	    					}	    					
	    				}
	    				Ext.getCmp('nextplan').setValue(values);
		   			} else{
		   				saveFailure();//@i18n/i18n.js
		   			}
		   		}		   		
			});
		}
	},
	getLast: function(lastworkplan){
		if(lastworkplan!=null && lastworkplan!=''){
			var id = lastworkplan.split('IS')[1];
			Ext.Ajax.request({
		   		url : basePath + 'oa/persontask/workPlan/getWorkPlanDetail.action',
		   		params : {
		   			id: id
		   		},
		   		method : 'post',
		   		async: false,
		   		callback : function(options,success,response){
//		   			me.getActiveTab().setLoading(false);
		   			var res = new Ext.decode(response.responseText);
	    			if(res.success){
	    				var values = '';
	    				for(var i=0; i<res.workplandetaillist.length; i++){
	    					if(i==res.workplandetaillist.length-1){
	    						values += res.workplandetaillist[i].wpd_plan;	    						
	    					} else {
	    						values += res.workplandetaillist[i].wpd_plan + '==###==';	 
	    					}
	    					
	    				}
	    				Ext.getCmp('lastplan').setValue(values);
		   			} else{
		   				saveFailure();//@i18n/i18n.js
		   			}
		   		}		   		
			});
		}
	}
});