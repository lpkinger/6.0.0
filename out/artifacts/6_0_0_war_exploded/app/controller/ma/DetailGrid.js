Ext.QuickTips.init();
Ext.define('erp.controller.ma.DetailGrid', {
    extend: 'Ext.app.Controller',
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    views:[
   		'ma.DetailGrid','ma.MyDetail',
   		'core.button.DeleteDetail', 'core.button.DbfindButton','core.button.ComboButton',
   		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.toolbar.Toolbar','core.grid.YnColumn'
   	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpDeleteDetailButton': {
    			afterrender: function(btn){
    				btn.ownerCt.add({
    					xtype:'erpDbfindButton'
    				});
    				btn.ownerCt.add({
    					xtype:'erpComboButton'
    				});
    			}
    		},
    		'mydetail': {
    			select: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record);
    				var grid = selModel.view.ownerCt.ownerCt;
    				if(record&&record.data.dg_dbbutton != '0') {
    					grid.down('erpDbfindButton').setDisabled(false); 
    					grid.down('erpComboButton').setDisabled(true);
    				}else if(record && (record.data.dg_type == 'combo' || record.data.dg_type=='editcombo')) {
    					grid.down('erpDbfindButton').setDisabled(true); 
    					grid.down('erpComboButton').setDisabled(false);
    				}else {
    					grid.down('erpComboButton').setDisabled(true);
    					grid.down('erpDbfindButton').setDisabled(true);
    				}
    			}
    		},
    		/**
    		 * 下拉框设置
    		 */
    		'erpComboButton': {
    			click: function(btn){
    				var grid = Ext.getCmp('mydetail'), record = grid.selModel.lastSelected;
    				if(record) {
    					 if(record.data.dg_type == 'combo' || record.data.dg_type =='editcombo') 
    						btn.comboSet((grid.whoami || record.data.dg_caller), record.data.dg_field);
    				}
    			}
    		},
    		/**
    		 * DBFind设置
    		 */
    		'erpDbfindButton': {
    			click: function(btn){
    				var grid = Ext.getCmp('mydetail'), record = grid.selModel.lastSelected;
    				if(record) {
        					btn.dbfindSetGrid((grid.whoami || record.data.dg_caller),grid, record.data.dg_field);
    				}
    			}
    		},
    		'dbfindtrigger': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    			}
    		},
    		'button[name=close]': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'button[name=save]': {
    			click: function(btn){
    				me.save();
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	isUnique: function(grid, fieldName) {
    	var keys = {}, key = null;
    	grid.store.each(function(item){
    		key = item.get(fieldName);
    		if(item.get('deploy') && key) {
    			key = key.toLowerCase();
    			keys[key] = (keys[key] || 0) + 1;
    		}
    	});
    	var fields = Ext.Object.getKeys(keys);
    	var err = Ext.Array.filter(fields, function(field){
    		return keys[field] > 1;
    	});
    	return err.join(',');
    },
    autoSetGridIgnore: function(item, dictionary) {
    	if(item.get('deploy') && item.get('dg_field') && dictionary && !item.get('dg_logictype')) {
    		var field = item.get('dg_field').toLowerCase();
        	if (field.indexOf(" ")>0) {// column有取别名
    			var strs = field.split(" ");
    			field = strs[strs.length - 1];
    		}
    		var d = Ext.Array.filter(dictionary, function(i){
    			return i.column_name == field;
    		});
    		if(d.length == 0)
    			item.set('dg_logictype', 'ignore');
    	}
    },
	save: function(){
		var me = this, isErr = false, isDefault = false;
		var detail =Ext.getCmp('mydetail');
		var items=detail.store.data.items ,table='',detno;
		Ext.each(items,function(item){
			if(item.data['deploy']==true){
				if(!detno){
					detno=item.data['dg_sequence'];
					table=item.data['dg_table'];
				}
				if(item.data['dg_sequence']<detno){
					detno=item.data['dg_sequence'];
					table=item.data['dg_table'];
				}	
			}
			
		});
		var gridAdded = [], gridUpdated = [], gridDeleted = [];
		var err = me.isUnique(detail, "dg_field");
   		if(err) {
   			showError("字段重复：" + err);
   			isErr = true;
   			return;
   		}
   		if(isDefault){
   			detail.store.each(function(item){
    			me.autoSetGridIgnore(item, detail.dictionary);
	   		});
  		}
  		de = detail.getChange();
		gridAdded = Ext.Array.merge(gridAdded, de.added);
		gridUpdated = Ext.Array.merge(gridUpdated, de.updated);
		gridDeleted = Ext.Array.merge(gridDeleted, de.deleted);
		if (isErr)
			return;
		me.onSave(table,unescape(Ext.encode(gridAdded).toString()), unescape(Ext.encode(gridUpdated).toString()), unescape(Ext.encode(gridDeleted).toString()));
    },
    onSave: function(table, gridAdded, gridUpdated, gridDeleted){
		var me = this;
    	me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'ma/updateDetail.action',
			params: {
				table: table,
				gridAdded: gridAdded,
				gridUpdated: gridUpdated,
				gridDeleted: gridDeleted
			},
			method : 'post',
			callback : function(opt, s, res){
				me.FormUtil.setLoading(false);
				var rs = new Ext.decode(res.responseText);
				if(rs.success){
					showMessage('提示', '保存成功!', 1000);
					window.location.reload();
				} else if(rs.exceptionInfo){
					var str = rs.exceptionInfo;
					if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
						str = str.replace('AFTERSUCCESS', '');
						window.location.reload();
					}
					showError(str);return;
				} else {
					showMessage('提示', '保存失败!');
				}
			}
		});
	}
});