Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.Bomlevel', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.bom.Bomlevel','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
    		'pm.bom.Maketypegrid','core.button.Add','core.button.Save','core.button.Close',
    		'pm.bom.Billtypegrid','core.button.Update','core.button.Delete','core.form.YnField',
    		'core.button.DeleteDetail','core.trigger.DbfindTrigger','core.grid.YnColumn',
    		'core.form.YnField','core.button.Bomleveldetail','core.button.ResAudit','core.button.Audit',
    		'core.button.Submit','core.button.ResSubmit'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record);
    			},
    			reconfigure: function(grid) {
    				Ext.defer(function(){
    					grid.readOnly = false;
    				}, 200);
    			}
    		},
    		'maketypegrid':{
    			itemclick: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record, 'maketypegrid');
    			},
    			reconfigure: function(grid) {
    				Ext.defer(function(){
    					grid.readOnly = false;
    				}, 200);
    			}
    		},
    		'billtypegrid':{
    			itemclick: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record, 'billtypegrid');
    			},
    			reconfigure: function(grid) {
    				Ext.defer(function(){
    					grid.readOnly = false;
    				}, 200);
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				this.beforeSave();
    			}
    		},
/*    		'field[name=bl_id]': {
    			afterrender: function(f){
    				if(f.value != null && f.value != ''){
    					var position = Ext.getCmp('maketypegrid');
    					position.getMyData(f.value);
						var reandpunish = Ext.getCmp('billtypegrid');
						reandpunish.getMyData(f.value);
    				}
    			},
    			change: function(f){
    				if(f.value != null && f.value != ''){
    					var position = Ext.getCmp('maketypegrid');
    					position.getMyData(f.value);
						var work = Ext.getCmp('billtypegrid');
						work.getMyData(f.value);
    				}
    			}
    		},*/
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.beforeUpdate();
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('bl_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addBomlevel', '新增BOM等级定义', 'jsps/pm/bom/Bomlevel.jsp');
    			}
    		},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('bl_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('bl_id').value,true);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('bl_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('bl_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('bl_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('bl_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('bl_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('bl_id').value);
				}
			},
    		'erpBomleveldetailButton':{
    			click: function(){
    				var detail = Ext.getCmp('grid');
    				var id = Ext.getCmp('bl_id').value;
    				var education = Ext.getCmp('maketypegrid');
    				var position = Ext.getCmp('billtypegrid');
    				var param1 = me.GridUtil.getAllGridStore(detail);
    				var param3 = me.GridUtil.getAllGridStore(education);
    				var param2 = me.GridUtil.getAllGridStore(position);
    				param1 = "[" + param1.toString() + "]";
    				param2 = "[" + param2.toString() + "]";
    				param3 = "[" + param3.toString() + "]";
    				warnMsg('确定要更新明细行数据么吗?', function(btn){
    					if (btn == 'yes') {
    						Ext.Ajax.request({
								url:basePath + "pm/bom/updateBomleveldetail.action",
								params:{
									param1:param1,
									param2:param2,
									param3:param3,
									id:id
								},
								method:'post',
								callback:function(options,success,response){
									var res = new Ext.decode(response.responseText);
									if(res.success){
										Ext.Msg.alert("提示", "更新明细行数据成功！");
									}else{
										Ext.Msg.alert("提示", "更新明细行数据失败！");
									}
								}
							});
    					}
    				});
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeSave: function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		var detail = Ext.getCmp('grid');
		var education = Ext.getCmp('maketypegrid');
		var position = Ext.getCmp('billtypegrid');
		var param1 = me.GridUtil.getGridStore(detail);
		var param3 = me.GridUtil.getGridStore(education);
		var param2 = me.GridUtil.getGridStore(position);
		/*if(detail.necessaryField.length > 0 && (param1.length == 0)){
			showError($I18N.common.grid.emptyDetail);
			return;
		} else {*/
			param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
			param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
			param3 = param3 == null ? [] : "[" + param3.toString().replace(/\\/g,"%") + "]";
			if(form.getForm().isValid()){
				Ext.each(form.items.items, function(item){
					if(item.xtype == 'numberfield'){
						if(item.value == null || item.value == ''){
							item.setValue(0);
						}
					}
				});
				var r = form.getValues();
				me.FormUtil.save(r, param1, param2, param3);
			}else{
				me.FormUtil.checkForm();
			}
		//}
	},
	beforeUpdate: function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		var detail = Ext.getCmp('grid');
		var education = Ext.getCmp('maketypegrid');
		var position = Ext.getCmp('billtypegrid');
		var param1 = me.GridUtil.getGridStore(detail);
		var param3 = me.GridUtil.getGridStore(education);
		var param2 = me.GridUtil.getGridStore(position);
		/*if(detail.necessaryField.length > 0 && (param1.length == 0)){
			showError($I18N.common.grid.emptyDetail);
			return;
		} else {*/
			param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
			param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
			param3 = param3 == null ? [] : "[" + param3.toString().replace(/\\/g,"%") + "]";
			if(form.getForm().isValid()){
				Ext.each(form.items.items, function(item){
					if(item.xtype == 'numberfield'){
						if(item.value == null || item.value == ''){
							item.setValue(0);
						}
					}
				});
				var r = form.getValues();
				me.FormUtil.update(r, param1, param2, param3);
			}else{
				me.FormUtil.checkForm();
			}
		//}
	},
	add10EmptyItems: function(grid){
		var items = grid.store.data.items;
		var detno = grid.detno;
		if(detno){
			var index = items.length == 0 ? 0 : Number(items[items.length-1].data[detno]);
			for(var i=0;i<10;i++){
				var o = new Object();
				o[detno] = index + i + 1;
				grid.store.insert(items.length, o);
				items[items.length-1]['index'] = items.length-1;
			}
		} else {
			for(var i=0;i<10;i++){
				var o = new Object();
				grid.store.insert(items.length, o);
				items[items.length-1]['index'] = items.length-1;
			}
		}
	}
});