Ext.QuickTips.init();
Ext.define('erp.controller.hr.attendance.WDSetAndChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.attendance.WDSetAndChange','hr.attendance.EmpTree2',
    		'core.form.YnField','core.grid.Panel2','core.button.BeforeUpdate',
    		'core.trigger.DbfindTrigger','core.grid.YnColumn','core.toolbar.Toolbar','core.form.Panel','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update',
  			'core.button.ResPost','core.trigger.CateTreeDbfindTrigger','core.trigger.TextAreaTrigger','core.button.AutoInvoice',
  			'core.form.MonthDateField'
    	],
    init:function(){
    	var me = this;
    	me.datamanager = [];
    	this.control({
    		
    		'field[name=startdate]':{
    			change:function(){
    				me.loadGridDate();		
    			}
    		},
    		'field[name=enddate]':{
    			change:function(){
    				me.loadGridDate();		
    			}
    		},
    		
    		'EmpTree2':{
    			itemmousedown:function(selModel,record){
    				if (record.data.leaf){
        				me.lastSelected = record;				//保存tree 所选择的数据
        				me.loadGridDate();							//根据条件加载grid 的数据
    					
    				}
    			}
    		},
    		'erpBeforeUpdateButton':{
    			click:function(btn){
    				
    				var grid = Ext.getCmp('grid');
    				console.log(grid);
    				
    			},
    			afterrender:function(btn){
    				btn.hide();
    			}
    			
    		}
    		
    	});
    },
    loadGridDate:function(){
    	var me = this;
    	var startdate = Ext.getCmp('startdate')?Ext.getCmp('startdate').value:null;
    	var enddate = Ext.getCmp('enddate')?Ext.getCmp('enddate').value:null;
    	var emid =me.lastSelected?me.lastSelected.data.id:null;
    	var grid = Ext.getCmp('grid');
    	if(emid!=null){
        	Ext.Ajax.request({
        		url : basePath + 'hr/attendance/loadGridDate.action',
        		params : {
        			startdate : startdate,
        			enddate : enddate,
        			emid : emid
        		},
        		async : false,
        		callback : function(options,success,response){
            		grid.setLoading(false);
            		var res = new Ext.decode(response.responseText);
            		if(res.exception || res.exceptionInfo){
            			showError(res.exceptionInfo);
            			return;
            		}
            		var data = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : [];//一定要去掉多余逗号，ie对此很敏感
            		console.log(data);
            		grid.store.loadData(data);
            	}
        	});
    		
    		
    	}

    	
    	
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
    getFormData: function(id, code){
    	
    }
});