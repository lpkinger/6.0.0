Ext.QuickTips.init();
Ext.define('erp.controller.crm.marketCompete.Competitor', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'crm.marketCompete.Competitor','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.grid.YnColumn','core.button.Scan',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
    			'core.trigger.DbfindTrigger','core.form.YnField','core.button.DeleteDetail','core.button.Upload','core.form.FileField',
    			'core.trigger.MultiDbfindTrigger','core.form.MultiField'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}			
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				me.FormUtil.onAdd('addCompetitor', '新增竞争对手', 'jsps/crm/marketCompete/competitor.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			},
    			afterrender:function(btn){
  				  var dbfind=getUrlParam('dbfind');
  					if(dbfind!=null){
  						me.dbfindAndSetValue('form',caller,dbfind.split('=')[0],dbfind);
  					}
  				}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){    			
    				me.FormUtil.onDelete((Ext.getCmp('co_id').value));
    			}
    		},
    		'htmleditor': {
    			afterrender: function(f){
    				f.setHeight(300);
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
	dbfindAndSetValue:function(which,caller,field,condition){//key:sc_chcode,condition:sc_chcode like '%1%'
		Ext.Ajax.request({
				url : basePath + 'common/autoDbfind.action',
				params: {
		   			which: which,
		   			caller: caller,
		   			field: field,
		   			condition: condition
		   		},
		   		async: false,
		   		method : 'post',
		   		callback : function(options,success,response){
		   			var res = new Ext.decode(response.responseText);
		   			if(res.exceptionInfo){
		   				showError(res.exceptionInfo);return;
		   			}
		   			if(!res.data){
		   				return;
		   			}
		   			var data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
		   			var form=Ext.getCmp('form');
		   			Ext.Array.each(res.dbfinds,function(db){
		   				if(Ext.getCmp(db.field)){
		   					Ext.getCmp(db.field).setValue(data[0][db.dbGridField]);
		   				}
		   			});
		   		}
		});
	}
});