Ext.QuickTips.init();
Ext.define('erp.controller.fs.fspledge.PledgeInout', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fs.fspledge.PledgeInout','core.form.Panel',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.Update','core.button.Delete',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
    		'core.form.FileField','common.datalist.GridPanel','common.datalist.Toolbar'
    	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('pledgeInout', '新增抵押物', 'jsps/fs/fspledge/pledgeInout.jsp');
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('pio_id').value);
    			}
    		},
			'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pio_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('pio_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pio_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('pio_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pio_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('pio_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pio_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('pio_id').value);
    			}
    		},
      		'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				},
				afterrender: function(btn){
					var value = Ext.getCmp('pio_class').value;
    				me.setDateFieldVisible(value);   //设置隐藏入库或者出库时间字段
    			}
			},
			'field[name=pio_class]':{
				change: function(th,newvalue){
					me.setDateFieldVisible(newvalue);    //设置隐藏入库或者出库时间字段
				}
			}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	setDateFieldVisible: function(value){
		if(value=='入库'){
			Ext.getCmp('pio_indate').setVisible(true);   //入库时间
			Ext.getCmp('pio_outdate').setVisible(false);    //出库时间
			Ext.getCmp('pio_expectdate').setVisible(false);    //预计回库时间
			Ext.getCmp('pio_realdate').setVisible(false);    //实际回库时间
		}else if(value=='出库'){
			Ext.getCmp('pio_indate').setVisible(false);   //入库时间
			Ext.getCmp('pio_outdate').setVisible(true);    //出库时间
			Ext.getCmp('pio_expectdate').setVisible(false);    //预计回库时间
			Ext.getCmp('pio_realdate').setVisible(false);    //实际回库时间
		}else if(value=='临时出库'){
			Ext.getCmp('pio_indate').setVisible(false);   //入库时间
			Ext.getCmp('pio_outdate').setVisible(false);    //出库时间
			Ext.getCmp('pio_expectdate').setVisible(true);    //预计回库时间
			Ext.getCmp('pio_realdate').setVisible(true);    //实际回库时间
		}else{   
			Ext.getCmp('pio_indate').setVisible(true);   //入库时间
			Ext.getCmp('pio_outdate').setVisible(true);    //出库时间
			Ext.getCmp('pio_expectdate').setVisible(true);    //预计回库时间
			Ext.getCmp('pio_realdate').setVisible(true);    //实际回库时间
		}
	}
});