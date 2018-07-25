Ext.QuickTips.init();
Ext.define('erp.controller.ma.Form', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'ma.Form','core.form.Panel','core.grid.Panel2','ma.MyGrid', 
   		'core.button.UUListener','core.button.Sync','erp.view.core.form.FileField',
   		'core.button.Add','core.button.Save','core.button.Close','core.button.Update','core.button.Delete','core.button.DeleteDetail',
   		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger',
   		'core.grid.TfColumn','core.button.DbfindButton','core.button.ComboButton', 'core.form.YnField'
   	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpSyncButton': {
    			afterrender: function(btn){
    				btn.autoClearCache = true;
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				me.save();
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				me.update();
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('fo_id').value);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'mygrid':{
    			select: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record);
    				var grid = selModel.view.ownerCt.ownerCt;
    				if(record && record.data.fd_dbfind !='F') {
    					grid.down('erpDbfindButton').setDisabled(false); 
    					grid.down('erpComboButton').setDisabled(true);
    				}else if(record && record.data.fd_type == 'C') {
    					grid.down('erpDbfindButton').setDisabled(true); 
    					grid.down('erpComboButton').setDisabled(false);
    				}else {
    					grid.down('erpComboButton').setDisabled(true);
    					grid.down('erpDbfindButton').setDisabled(true);
    				}
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addForm', '新增Form', 'jsps/ma/form.jsp');
    			}
    		},
    		'erpDeleteDetailButton': {
    			afterrender: function(btn){
    				btn.ownerCt.add({
    					xtype:'erpDbfindButton',
    				});
    				btn.ownerCt.add({
    					xtype:'erpComboButton',
    				});
    			}
    		},
    		/**
    		 * 下拉框设置
    		 */
    		'erpComboButton': {
    			click: function(btn){
    				var record = btn.ownerCt.ownerCt.selModel.lastSelected;
    				if(record && record.data.fd_type == 'C') {   					
        					btn.comboSet(Ext.getCmp('fo_caller').value, record.data.fd_field,me);
    				}
    			}
    		},
    		/**
    		 * DBFind设置
    		 */
    		'erpDbfindButton': {
    			click: function(btn){
    				var grid = btn.ownerCt.ownerCt, record = grid.selModel.lastSelected;
    				if(record && record.data.fd_dbfind != 'F') {
    					btn.dbfindSetUI(Ext.getCmp('fo_caller').value, record.data.fd_field, grid);
    				}
    			}
    		},
    		'erpFormPanel textfield[name=fo_detailtable]': {
    			change: function(field){
    				field.setValue(field.value.toUpperCase());
    			}
    		},
    		'multidbfindtrigger': {
    			render: function(field){
    				if(field.name == 'fo_button4add' || field.name == 'fo_button4rw'){
    					var fields = Ext.Object.getKeys($I18N.common.button);
    					var values = Ext.Object.getValues($I18N.common.button);
    					var data = [];
    					Ext.each(fields, function(f, index){
    						var o = {};
    						o.value = fields[index];
    						o.display = values[index];
    						data.push(o);
    					});
    					field.multistore = {fields:['display', 'value'],data:data};
    				}
    			}
    		},
    		'multidbfindtrigger[name=fd_field]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.ComponentQuery.query('mygrid')[0].selModel.getLastSelected();
    				var code = record.get('fd_table');
    				if(code == null || code == ''){
    					showError("请先选择表单!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "ddd_tablename='" + code.split(' ')[0].toUpperCase() + "'";
    				}
    			},
    			aftertrigger: function(t) {
    				var f = Ext.getCmp('fo_table');
    				var record = Ext.ComponentQuery.query('mygrid')[0].selModel.getLastSelected();
    				var a = record.data['fd_table'];
    				if(Ext.isEmpty(f.value) && !Ext.isEmpty(a)) {
    					f.setValue(a.toUpperCase());
    				}
    			}
    		},
    		'dbfindtrigger[name=fo_table]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    			}
    		},
    		'dbfindtrigger[name=fd_table]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    			},
    			aftertrigger: function(t) {
    				var f = Ext.getCmp('fo_table');
    				if(Ext.isEmpty(f.value) && !Ext.isEmpty(t.value)) {
    					f.setValue(t.value.toUpperCase());
    				}
    			}
    		},
    		'dbfindtrigger[name=fo_keyfield]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.findConfig = function() {
    					var tab = Ext.getCmp('fo_table').getValue();
    					if(tab)
    						return "ddd_tablename='" + tab.split(' ')[0].toUpperCase() + "'";
    					return null;
    				};
    			}
    		},
    		'dbfindtrigger[name=fo_codefield]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.findConfig = function() {
    					var tab = Ext.getCmp('fo_table').getValue();
    					if(tab)
    						return "ddd_tablename='" + tab.split(' ')[0].toUpperCase() + "'";
    					return null;
    				};
    			}
    		},
    		'dbfindtrigger[name=fo_statusfield]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.findConfig = function() {
    					var tab = Ext.getCmp('fo_table').getValue();
    					if(tab)
    						return "ddd_tablename='" + tab.split(' ')[0].toUpperCase() + "'";
    					return null;
    				};
    			}
    		},
    		'dbfindtrigger[name=fo_statuscodefield]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.findConfig = function() {
    					var tab = Ext.getCmp('fo_table').getValue();
    					if(tab)
    						return "ddd_tablename='" + tab.split(' ')[0].toUpperCase() + "'";
    					return null;
    				};
    			}
    		},
    		'dbfindtrigger[name=fo_detailkeyfield]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.findConfig = function() {
    					var tab = Ext.getCmp('fo_detailtable').getValue();
    					if(tab)
    						return "ddd_tablename='" + tab.split(' ')[0].toUpperCase() + "'";
    					return null;
    				};
    			}
    		},
    		'dbfindtrigger[name=fo_detailmainkeyfield]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.findConfig = function() {
    					var tab = Ext.getCmp('fo_detailtable').getValue();
    					if(tab)
    						return "ddd_tablename='" + tab.split(' ')[0].toUpperCase() + "'";
    					return null;
    				};
    			}
    		},
    		'dbfindtrigger[name=fo_detailstatuscode]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.findConfig = function() {
    					var tab = Ext.getCmp('fo_detailtable').getValue();
    					if(tab)
    						return "ddd_tablename='" + tab.split(' ')[0].toUpperCase() + "'";
    					return null;
    				};
    			}
    		},
    		'dbfindtrigger[name=fo_detailstatus]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.findConfig = function() {
    					var tab = Ext.getCmp('fo_detailtable').getValue();
    					if(tab)
    						return "ddd_tablename='" + tab.split(' ')[0].toUpperCase() + "'";
    					return null;
    				};
    			}
    		},
    		'dbfindtrigger[name=fo_detaildetnofield]': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    				t.findConfig = function() {
    					var tab = Ext.getCmp('fo_detailtable').getValue();
    					if(tab)
    						return "ddd_tablename='" + tab.split(' ')[0].toUpperCase() + "'";
    					return null;
    				};
    			}
    		},
    		'mfilefield': {
    			afterrender: function(f) {
    				// 只能有一个rpt附件
    				f.multi = false;
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onChange: function(field, value){
		field.setValue(value);
		if(value == 'C'){
			
		}
	},
	save: function(){
		//序列号
		Ext.getCmp('fo_seq').setValue(Ext.getCmp('fo_table').value.toUpperCase() + '_SEQ');
		var dt = Ext.getCmp('fo_detailtable').value;
		if(dt != null && dt != ''){
			Ext.getCmp('fo_detailseq').setValue(dt.toUpperCase() + '_SEQ');
			var dm = Ext.getCmp('fo_detailmainkeyfield').value;
			if(dm == null || dm == ''){
				showError("请选择从表与主表关联的字段!");return;
			}
		}
		var grid = Ext.ComponentQuery.query('mygrid')[0], items = grid.store.data.items, dd = new Array(), d = null;
		var field = Ext.getCmp('fo_table');
		Ext.Array.each(items, function(item){
			d = item.data;
			if(!Ext.isEmpty(d['fd_field'])){
				if(Ext.isEmpty(d['fd_table']))
						item.set('fd_table', field.value);
				d.fd_readonly = d.fd_readonly ? 'T' : 'F';
				d.fd_dbfind = d.fd_dbfind ? d.fd_dbfind : 'F';
				d.fd_allowblank = d.fd_allowblank ? 'T' : 'F';
				d.fd_modify = d.fd_modify ? 'T' : 'F';
				d.fd_check = d.fd_check ? 1 : 0;
				dd.push(d);
			}
		});
		if(dd.length > 0) {
			var form = Ext.getCmp('form');
			this.FormUtil.getSeqId(form);
			this.FormUtil.save(form.getValues(), Ext.encode(dd));
		} else {
			showError('请至少配置一个有效字段!');
		}
		
	},
	update: function(){
		var grid = Ext.ComponentQuery.query('mygrid')[0];
		var field = Ext.getCmp('fo_table'), id = Ext.getCmp('fo_id').value;
		grid.store.each(function(item){
			if(item.get('deploy') && !Ext.isEmpty(item.get('fd_field'))){
				if(item.get('fd_foid') != id)
					item.set('fd_foid', id);
				if(Ext.isEmpty(item.get('fd_table')))
					item.set('fd_table', field.value);
			}
		});
		var me = this;
		if(! me.FormUtil.checkForm()){
			return;
		}
		var dd = grid.getChange();
		me.FormUtil.update(Ext.getCmp('form').getValues(), Ext.encode(dd.added), 
				Ext.encode(dd.updated), Ext.encode(dd.deleted));
	}	
});