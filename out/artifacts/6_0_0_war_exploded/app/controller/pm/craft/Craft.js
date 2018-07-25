Ext.QuickTips.init();
Ext.define('erp.controller.pm.craft.Craft', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.craft.Craft','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
    		'core.button.Update','core.button.Delete','core.button.Scan','core.form.YnField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				if(!Ext.getCmp('grid').readOnly){
    					this.onGridItemClick(selModel, record);
    				}
    			},
    			afterrender:function(){
    				me.BaseUtil.getSetting('sys', 'pricePerTime', function(v) {
						if(v){
							pricePerTime = v;						
						}
					});
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				me.beforeSaveOrUpdate(btn);
					setTimeout(function(){
						me.FormUtil.beforeSave(me);
					},200);
    				
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				me.beforeSaveOrUpdate(btn);
    				setTimeout(function(){
    					me.FormUtil.onUpdate(me);
    				},200);
    				
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('cr_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addCraft', '新增工艺资料', 'jsps/pm/craft/craft.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('cr_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('cr_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('cr_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('cr_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('cr_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('cr_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('cr_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('cr_id').value);
    			}
    		},
    		
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeSaveOrUpdate: function(btn){
		var me = this;
		var grid = Ext.getCmp('grid');
		var items = grid.getStore().data.items;//获取store里面的数据
		var sum_price = 0;
		var sum_count = 0;
		Ext.each(items,function(item,index){
			if(!me.GridUtil.isBlank(grid, item.data)){
				sum_price = sum_price + Number(item.data['cd_price']);
				sum_count ++;
			}
		});
		if(Ext.getCmp('cr_price')){
		    Ext.getCmp('cr_price').setValue(sum_price.toFixed(4));
		}
		if(Ext.getCmp('cr_stepcount')){
			Ext.getCmp('cr_stepcount').setValue(sum_count);
		}
	    var form = me.getForm(btn);
		if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
			me.BaseUtil.getRandomNumber();//自动添加编号
		}
		
	}
});