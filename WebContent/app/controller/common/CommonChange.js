Ext.QuickTips.init();
Ext.define('erp.controller.common.CommonChange', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
      		'common.CommonChange.Form','common.CommonChange.ViewPort','core.grid.Panel2','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
      		'core.button.Scan','core.button.Banned','core.button.ResBanned','core.form.MultiField','core.button.Confirm','core.button.Sync',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn','core.trigger.AddDbfindTrigger',
      		'core.form.FileField','core.form.SplitTextField','core.button.End','core.button.ResEnd','core.form.CheckBoxGroup'
      	],
    init:function(){
    	var me = this;
    	me.FormUtil = Ext.create('erp.util.FormUtil');
    	me.GridUtil = Ext.create('erp.util.GridUtil');
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({
    		'erpFormPanel': {
    			afterload: function(form){
    				var main = getUrlParam('main');
					formCondition = getUrlParam('formCondition');
					if(main&&!formCondition){
						var field = '';
						if(caller == 'Vendor$Change'){
							field = 've_code';
						}else if(caller =='Customer!Base$Change'){
							field = 'cu_code';
						}
						me.FormUtil.autoDbfind(caller, field, main);
					}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(!Ext.isEmpty(form.codeField) && Ext.getCmp(form.codeField) && ( 
    						Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == '')){
    					me.BaseUtil.getRandomNumber(caller);//自动添加编号
    				}
    				//消除公司和简称字段前后空格
    				if (caller == 'Customer!Base$Change') {
    					var new_name = Ext.getCmp('cu_name-new');
    					var new_short = Ext.getCmp('cu_shortname-new');
    					this.trim(new_name);
    					this.trim(new_short);
    				}
    				if (caller == 'Vendor$Change') {
    					var new_name = Ext.getCmp('ve_name-new');
    					var new_short = Ext.getCmp('ve_shortname-new');
    					this.trim(new_name);
    					this.trim(new_short);
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'mfilefield':{
    			beforerender:function(field){
    				field.autoUpdate=false;
    			}
    		},
    		'dbfindtrigger':{
    			afterrender:function(field){
    				//考虑将来 不同变更单变更不同类型 如物料
    				field.dbCaller=field.logic=='changeCodeField'?caller:caller.split('$')[0];
    				
    			},
    			aftertrigger: function(f, r) {
            		var form=f.ownerCt;
            		if(form && form.changeCodeField==f.name){
            		  form.loadInitData(form,f);          			
            		}                  
            	}
    		},
    		'erpDeleteButton' : {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				//消除公司和简称字段前后空格
    				if (caller == 'Customer!Base$Change') {
    					var new_name = Ext.getCmp('cu_name-new');
    					var new_short = Ext.getCmp('cu_shortname-new');
    					this.trim(new_name);
    					this.trim(new_short);
    				}
    				if (caller == 'Vendor$Change') {
    					var new_name = Ext.getCmp('ve_name-new');
    					var new_short = Ext.getCmp('ve_shortname-new');
    					this.trim(new_name);
    					this.trim(new_short);
    				}
    				this.FormUtil.onUpdate(this);
    			},
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){   		
    				var url = window.location.href;
    				url = url.replace(basePath, '');
    				url = url.substring(0, url.lastIndexOf('formCondition')-1);
    				url = me.FormUtil.contains(url, '?', true)?url : url+"?whoami="+caller;
    				me.FormUtil.onAdd('add' + caller, '新增单据', url);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	trim: function(field) {
		if (field) {
			var value = field.value;
			if (value) {
				value = value.replace(/(^\s*)|(\s*$)/g, "");
				field.setValue(value);
			}
		}
	}
	
});