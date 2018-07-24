Ext.QuickTips.init();
Ext.define('erp.controller.hr.emplmana.Archive', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'hr.emplmana.Archive','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','hr.emplmana.RelationGrid',
    		'hr.emplmana.EducationGrid','hr.emplmana.PositionGrid','hr.emplmana.WorkGrid','hr.emplmana.TrainingGrid','hr.emplmana.EmpsjobsGrid',
    		'core.button.Add','core.button.Save','core.button.Close','hr.emplmana.ReandpunishGrid',
    		'core.button.Update','core.button.Delete','core.form.YnField','core.button.DeleteDetail','core.button.ResAudit',
    		'core.trigger.DbfindTrigger','core.grid.YnColumn','core.form.YnField','core.trigger.TextAreaTrigger',
  			'core.button.Audit','core.button.Submit','core.button.ResSubmit','core.form.FileField','hr.emplmana.ContractGrid'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record);
    			}
    		},
    		'reandpunishgrid':{
    			itemclick: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record, 'reandpunishgrid');
    			}
    		},
    		'positiongrid':{
    			itemclick: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record, 'positiongrid');
    			}
    		},
    		'workgrid':{
    			itemclick: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record, 'workgrid');
    			}
    		},
    		'educationgrid':{
    			itemclick: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record, 'educationgrid');
    			}
    		},
    		'relationgrid':{
    			itemclick: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record, 'relationgrid');
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				this.beforeSave();
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('em_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('em_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('em_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('em_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('em_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('em_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('em_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('em_id').value);
    			}
    		},
    		'field[name=em_id]': {
    			afterrender: function(f){
    				if(f.value != null && f.value != ''){
    					var emid = Ext.getCmp('em_id').value;
    					var positiongrid = Ext.getCmp('positiongrid');
    					positiongrid.getMyData(emid);
    					var workgrid  = Ext.getCmp('workgrid');
    					workgrid.getMyData(emid);
						var reandpunishgrid = Ext.getCmp('reandpunishgrid');
						reandpunishgrid.getMyData(emid);
						var educationgrid = Ext.getCmp('educationgrid');
						educationgrid.getMyData(emid);
						var relationgrid = Ext.getCmp('relationgrid');
						relationgrid.getMyData(emid);
						var contractgrid = Ext.getCmp('contractgrid');
						if(contractgrid!=null)contractgrid.getMyData(emid);
						var traininggrid = Ext.getCmp('traininggrid');
						if(traininggrid!=null)traininggrid.getMyData(emid);
						var empsjobsgrid = Ext.getCmp('empsjobsgrid');
						if(empsjobsgrid!=null)empsjobsgrid.getMyData(emid);
    				}
    			},
    			change: function(f){
    				if(f.value != null && f.value != ''){
    					var emid = Ext.getCmp('em_id').value;
    					Ext.getCmp('positiongrid').getMyData(emid);
						Ext.getCmp('workgrid').getMyData(emid);
						Ext.getCmp('reandpunishgrid').getMyData(emid);
						Ext.getCmp('educationgrid').getMyData(emid);
						if(Ext.getCmp('contractgrid')!=null)Ext.getCmp('contractgrid').getMyData(emid);
						//Ext.getCmp('relationgrid').getMyData(emid);
    				}
    			}
    		},
    		'field[name=em_iccode]':{
    			blur:function(f){
    				if(f.value != null && f.value != ''){
    					if(f.value.length!=18){
    						showError('身份证填写不正确！');
    						return;
    					}
    					var year=f.value.slice(6,10);
    					var month=f.value.slice(10,12);
    					var day=f.value.slice(12,14);
    					var year_int=year-0;
    					var month_int=month-0;
    					var day_int=day-0;
    					if(isNaN(year_int)||isNaN(month_int)||isNaN(day_int)){
    						showError('身份证填写不正确！');
    						return;
    					}
    					if(month_int>12||day_int>31){
    						showError('身份证填写不正确！');
    						return;
    					}
    					Ext.getCmp('em_birthday').setValue(year+'-'+month+'-'+day);
    				}
    			}
    		},
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
    				me.FormUtil.onDelete(Ext.getCmp('em_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addArchive', '新增人员档案', 'jsps/hr/emplmana/employee/archive.jsp');
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
		var education = Ext.getCmp('educationgrid');
		var position = Ext.getCmp('positiongrid');
		var work = Ext.getCmp('workgrid');
		var reandpunish = Ext.getCmp('reandpunishgrid');
		var relation = Ext.getCmp('relationgrid');
		var param1 = me.GridUtil.getGridStore(detail);
		var param2 = me.GridUtil.getGridStore(education);
		var param3 = me.GridUtil.getGridStore(position);
		var param4 = me.GridUtil.getGridStore(work);
		var param5 = me.GridUtil.getGridStore(reandpunish);
		var param6 = me.GridUtil.getGridStore(relation);
		if(param6.length==0&&Ext.getCmp('em_havefriend').value=='有'){
			showError('请填写亲友信息！');
			return;
		}
		if(detail.necessaryField.length > 0 && (param1.length == 0)){// 
			showError($I18N.common.grid.emptyDetail);
			return;
		} else {
			param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
			param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
			param3 = param3 == null ? [] : "[" + param3.toString().replace(/\\/g,"%") + "]";
			param4 = param4 == null ? [] : "[" + param4.toString().replace(/\\/g,"%") + "]";
			param5 = param5 == null ? [] : "[" + param5.toString().replace(/\\/g,"%") + "]";
			param6 = param6 == null ? [] : "[" + param6.toString().replace(/\\/g,"%") + "]";
			if(form.getForm().isValid()){
				Ext.each(form.items.items, function(item){
					if(item.xtype == 'numberfield'){
						if(item.value == null || item.value == ''){
							item.setValue(0);
						}
					}
				});
				var r = form.getValues();
				me.FormUtil.save(r, param1, param2, param3,param4,param5,param6);
			}else{
				me.FormUtil.checkForm();
			}
		}
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
		var education = Ext.getCmp('educationgrid');
		var position = Ext.getCmp('positiongrid');
		var work = Ext.getCmp('workgrid');
		var reandpunish = Ext.getCmp('reandpunishgrid');
		var relation = Ext.getCmp('relationgrid');
		var param1 = me.GridUtil.getGridStore(detail);
		var param2 = me.GridUtil.getGridStore(education);
		var param3 = me.GridUtil.getGridStore(position);
		var param4 = me.GridUtil.getGridStore(work);
		var param5 = me.GridUtil.getGridStore(reandpunish);
		var param6 = me.GridUtil.getGridStore(relation);
		if(detail.necessaryField.length > 0 && (param1.length == 0)){
			showError($I18N.common.grid.emptyDetail);
			return;
		} else {
			param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
			param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
			param3 = param3 == null ? [] : "[" + param3.toString().replace(/\\/g,"%") + "]";
			param4 = param4 == null ? [] : "[" + param4.toString().replace(/\\/g,"%") + "]";
			param5 = param5 == null ? [] : "[" + param5.toString().replace(/\\/g,"%") + "]";
			param6 = param6 == null ? [] : "[" + param6.toString().replace(/\\/g,"%") + "]";
			if(form.getForm().isValid()){
				Ext.each(form.items.items, function(item){
					if(item.xtype == 'numberfield'){
						if(item.value == null || item.value == ''){
							item.setValue(0);
						}
					}
				});
				var r = form.getValues();
				me.FormUtil.update(r, param1, param2, param3,param4,param5,param6);
			}else{
				me.FormUtil.checkForm();
			}
		}
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
	},
	onGridItemClick2: function(selModel, record){
		var grid = Ext.getCmp('positiongrid');
		var index = null;
		if(grid.detno){
			index = record.data[grid.detno];
			index = index == null ? (record.index + 1) : index;
			if(index.toString() == 'NaN'){
				index = '';
			}
			if(index == grid.store.data.items[grid.store.data.items.length-1].data[grid.detno]){//如果选择了最后一行
				this.add10EmptyItems(grid);//就再加10行
	    	}
		} else {
			index = record.index + 1;
			if(index.toString() == 'NaN'){
				index = '';
			}
			if(index == grid.store.data.items[grid.store.data.items.length-1].index + 1){//如果选择了最后一行
	    		this.add10EmptyItems(grid);//就再加10行
	    	}
		}
		var btn = Ext.ComponentQuery.query('erpDeleteDetailButton')[0];
		btn.setDisabled(false);
		btn.setText(btn.text.split(':')[0] + ":" + index);
		btn = Ext.ComponentQuery.query('copydetail')[0];
		btn.setDisabled(false);
		btn.setText(btn.text.split(':')[0] + ":" + index);
		btn = Ext.ComponentQuery.query('pastedetail')[0];
		btn.setDisabled(false);
		btn.setText(btn.text.split(':')[0] + ":" + index);
		btn = Ext.ComponentQuery.query('updetail')[0];
		btn.setDisabled(false);
		btn.setText(btn.text.split(':')[0] + ":" + index);
		btn = Ext.ComponentQuery.query('downdetail')[0];
		btn.setDisabled(false);
		btn.setText(btn.text.split(':')[0] + ":" + index);
	},
	onGridItemClick3: function(selModel, record){
		var grid = Ext.getCmp('workgrid');
		var index = null;
		if(grid.detno){
			index = record.data[grid.detno];
			index = index == null ? (record.index + 1) : index;
			if(index.toString() == 'NaN'){
				index = '';
			}
			if(index == grid.store.data.items[grid.store.data.items.length-1].data[grid.detno]){//如果选择了最后一行
				this.add10EmptyItems(grid);//就再加10行
	    	}
		} else {
			index = record.index + 1;
			if(index.toString() == 'NaN'){
				index = '';
			}
			if(index == grid.store.data.items[grid.store.data.items.length-1].index + 1){//如果选择了最后一行
	    		this.add10EmptyItems(grid);//就再加10行
	    	}
		}
		var btn = Ext.ComponentQuery.query('erpDeleteDetailButton')[0];
		btn.setDisabled(false);
		btn.setText(btn.text.split(':')[0] + ":" + index);
		btn = Ext.ComponentQuery.query('copydetail')[0];
		btn.setDisabled(false);
		btn.setText(btn.text.split(':')[0] + ":" + index);
		btn = Ext.ComponentQuery.query('pastedetail')[0];
		btn.setDisabled(false);
		btn.setText(btn.text.split(':')[0] + ":" + index);
		btn = Ext.ComponentQuery.query('updetail')[0];
		btn.setDisabled(false);
		btn.setText(btn.text.split(':')[0] + ":" + index);
		btn = Ext.ComponentQuery.query('downdetail')[0];
		btn.setDisabled(false);
		btn.setText(btn.text.split(':')[0] + ":" + index);
	},
	onGridItemClick4: function(selModel, record){
		var grid = Ext.getCmp('educationgrid');
		var index = null;
		if(grid.detno){
			index = record.data[grid.detno];
			index = index == null ? (record.index + 1) : index;
			if(index.toString() == 'NaN'){
				index = '';
			}
			if(index == grid.store.data.items[grid.store.data.items.length-1].data[grid.detno]){//如果选择了最后一行
				this.add10EmptyItems(grid);//就再加10行
	    	}
		} else {
			index = record.index + 1;
			if(index.toString() == 'NaN'){
				index = '';
			}
			if(index == grid.store.data.items[grid.store.data.items.length-1].index + 1){//如果选择了最后一行
	    		this.add10EmptyItems(grid);//就再加10行
	    	}
		}
		var btn = Ext.ComponentQuery.query('erpDeleteDetailButton')[0];
		btn.setDisabled(false);
		btn.setText(btn.text.split(':')[0] + ":" + index);
		btn = Ext.ComponentQuery.query('copydetail')[0];
		btn.setDisabled(false);
		btn.setText(btn.text.split(':')[0] + ":" + index);
		btn = Ext.ComponentQuery.query('pastedetail')[0];
		btn.setDisabled(false);
		btn.setText(btn.text.split(':')[0] + ":" + index);
		btn = Ext.ComponentQuery.query('updetail')[0];
		btn.setDisabled(false);
		btn.setText(btn.text.split(':')[0] + ":" + index);
		btn = Ext.ComponentQuery.query('downdetail')[0];
		btn.setDisabled(false);
		btn.setText(btn.text.split(':')[0] + ":" + index);
	}
});