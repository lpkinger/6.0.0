Ext.QuickTips.init();
Ext.define('erp.controller.hr.attendance.CCSQChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'hr.attendance.CCSQChange','core.form.Panel','core.form.FileField','core.form.MultiField','core.form.CheckBoxGroup','core.trigger.MultiDbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','core.grid.Panel2',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar','core.button.Confirm',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger','core.trigger.AddDbfindTrigger'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
			'hidden[name=cc_cqid]': {
    			change: function(field){
    				console.log('hidden');
    				if(field != null && field != ''){
        				Ext.getCmp('cc_newplace').setValue(Ext.getCmp('cc_oldplace').value);
        				Ext.getCmp('cc_newline').setValue(Ext.getCmp('cc_oldline').value);
        				Ext.getCmp('cc_newremark').setValue(Ext.getCmp('cc_oldremark').value);
    				}
    			}
    		},
    		'dbfindtrigger[name=cd_cqdetno]': {
    			afterrender: function(t){
    				t.gridKey = "cc_cqid";
    				t.mappinggirdKey = "fpd_fpid";
    				t.gridErrorMessage = "请先选择出差申请单!";
    			},
    			aftertrigger: function(t){
    				if(t.value != null && t.value != ''){
    					if(t.owner) {
    						var record = t.owner.selModel.lastSelected;
    						record.set('cd_newstartdate', record.data.cd_oldstartdate);
        					record.set('cd_newenddate', record.data.cd_oldenddate);
        					record.set('cd_newn5', record.data.cd_oldn5);
        					record.set('cd_newn6', record.data.cd_oldn6);
        					record.set('cd_newvehicle', record.data.cd_oldvehicle);
            	    		record.set('cd_newplace', record.data.cd_oldplace);
            	    		record.set('cd_newd2', record.data.cd_oldd2);
    					}
    				}
    			}
    		},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					//保存之前的一些前台的逻辑判定
					this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('cc_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addCCSQChange', '新增出差申请变更单', 'jsps/hr/attendance/cCSQChange.jsp');
				}
			},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cc_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('cc_id').value);
				}
			},'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('cc_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('cc_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cc_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('cc_id').value);
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			}
    	});
	},
	onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});