Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.FeatureTemplet', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.bom.FeatureTemplet','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Upload','core.button.LoadFeature',
      		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
  			'core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				if(!Ext.getCmp('grid').readOnly){
    					this.onGridItemClick(selModel, record);
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items;
					var ftid = Ext.getCmp('ft_id').value;
					var i=0;
				    Ext.Array.each(items, function(item){
				    	item.set('fd_ftid', ftid);
				    	if (item.data['fd_iskey'] != 0) {
							i++;
						}
					});
					if (i > 1) {
						showError('关键特征只能选择一个,请重新选择！');
						return;
					}
				    if(i == 0){
				    	Ext.Msg.alert("提示","请选择关键特征！");
				    	return;
				    }
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items;
					var ftid = Ext.getCmp('ft_id').value;
					var i=0;
				    Ext.Array.each(items, function(item){
				    	item.set('fd_ftid', ftid);
				    	if (item.data['fd_iskey'] != 0) {
							i++;
						}
					});
					if (i > 1) {
						showError('关键特征只能选择一个,请重新选择！');
						return;
					}
				    if(i == 0){
				    	Ext.Msg.alert("提示","请选择关键特征！");
				    	return;
				    }
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ft_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addFeatureTemplet', '新增物料模板', 'jsps/pm/bom/FeatureTemplet.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ft_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('ft_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ft_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('ft_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ft_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('ft_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ft_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ft_id').value);
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