Ext.QuickTips.init();
Ext.define('erp.controller.b2b.sale.ZDQuotation', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','b2b.sale.ZDQuotation','core.grid.Panel2','core.toolbar.Toolbar','core.form.FileField','core.form.MultiField',
      		'core.button.Save','core.button.Update','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload',
  				'core.button.Audit','core.button.Close','core.button.Delete','core.button.DeleteDetail','core.button.ResSubmit',
  				'core.button.ResAudit','core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.grid.YnColumn'
      	],
    init:function(){
    	var me = this;
    	me.alloweditor = true;
    	this.control({ 
    		'erpGridPanel2': { 
    			afterrender: function(grid){
    				var status = Ext.getCmp('qu_statuscode');
    				if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
    					Ext.each(grid.columns, function(c){
    						c.setEditor(null);
    					});
    				}
    				Ext.defer(function(){
    					var f = Ext.getCmp('qu_id');
    					if(f && f.value > 0)
    						me.getStepWise(f.value, function(data){
    							grid.store.each(function(d){
    								var dets = [], id = d.get('qd_id');
    								if(id && id > 0) {
    									Ext.Array.each(data, function(t){
    										if(t.qdd_qdid == id)
    											dets.push(t);
    									});
    									d.set('dets', dets);
    								}
    							});
    						});
    				}, 50);
    			},
    			itemclick: this.onGridItemClick
    		},
    		'field[name=qu_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=qu_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber(caller);//自动添加编号
    				}    				
    				this.beforeSave();    				
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var bool = true;
    				//供应商必填
    				var grid = Ext.getCmp('grid'),
    					fromDate = Ext.getCmp('qu_recorddate').value,
    					end = Ext.getCmp('qu_enddate').value;
    				if(end < new Date()){
    					bool=false;
    					showError('有效期小于当前日期，不能更新!');return;
    				}
    				grid.getStore().each(function(item){});
    				if(bool){
    					this.beforeUpdate();
    				}
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('qu_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addQuotation', '新增报价单', 'jsps/b2b/sale/zdquotation.jsp');
    			}
    		},    		
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('qu_statuscode');    				
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}  				
    			},
    			click: function(btn){
    				var end = Ext.getCmp('qu_enddate').value;
    				if(end < new Date()){
    					bool=false;
    					showError('有效期小于当前日期，不能提交!');return;
    				}    				
    				me.FormUtil.onSubmit(Ext.getCmp('qu_id').value); 
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('qu_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('qu_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('qu_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('qu_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('qu_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('qu_id').value);
    			}
    		},
    		'dbfindtrigger[name=qu_custcontact]': {
     			afterrender:function(trigger){
 	    			trigger.dbKey='qu_custcode';
 	    			trigger.mappingKey='ct_cucode';
 	    			trigger.dbMessage='请先选客户编号！';
     			}
     		},
    		'erpPrintButton': {
    			click: function(btn){
    				me.FormUtil.onPrint(Ext.getCmp('qu_id').value);
    			}
    		},    		 		
    		/**
    		 * 分段报价按钮
    		 */
    		'#stepWiseQuotation' : {
    			afterrender: function(b) {
    				Ext.defer(function(){
    					var f = Ext.getCmp('qu_statuscode');
    					if(f && f.value != 'ENTERING')
    						b.hide();
    				}, 100);
    			},
    			click: function(b) {
    				var record = b.ownerCt.ownerCt.selModel.lastSelected;
    				if(record)
    					me.onStepWiseClick(record);
    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    	var grid=selModel.ownerCt;
    	var show = false;
    	Ext.Array.each(grid.necessaryFields, function(field) {
    		var fieldValue=record.data[field];
    		if(fieldValue==undefined||fieldValue==""||fieldValue==null){
    			show = true;
    			return; 
    		}
        });
    	if(show){    		
        	var btn = Ext.getCmp('stepWiseQuotation');
        	btn && btn.setDisabled(true);
    	} else {    		
        	var btn = Ext.getCmp('stepWiseQuotation');
        	btn && btn.setDisabled(false);
		}
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	/**
	 * 分段报价
	 */
	onStepWiseClick: function(record) {
		var fields = this.getStepFields(record.get('dets')), me = this;
		Ext.create('Ext.window.Window', {
			autoShow: true,
			title: '分段报价',
			width: 300,
			height: 400,
			layout: 'anchor',
			items: [{
				anchor: '100% 100%',
				xtype: 'form',
				items: fields,
				bodyStyle: 'background: #f1f2f5;',
				defaults: {
					margin: '5'
				}
			}],
			buttonAlign: 'center',
			buttons: [{
				text: '确定',
				handler: function(b) {
					me.onStepConfirm(record, b.ownerCt.ownerCt.down('form'), function(){
						b.ownerCt.ownerCt.close();
					});
				}
			}, {
				text: '取消',
				handler: function(b) {
					b.ownerCt.ownerCt.close();
				}
			}]
		});
	},
	getStepFields: function(dets) {
		if(!dets || dets.length == 0)
			dets = [{qdd_lapqty: 0},{},{},{},{}];
		var fields = [], me = this;
		Ext.Array.each(dets, function(d){
			fields.push({
				xtype: 'fieldcontainer',
				layout: 'hbox',
				dataId: d.qdd_id,
				items: [{
					xtype: 'numberfield',
					fieldLabel: '数量 ≥ ',
					labelWidth: 60,
					hideTrigger: true,
					name: 'qdd_lapqty',
					value: d.qdd_lapqty,
					editable: (d.qdd_lapqty == null || d.qdd_lapqty > 0),
					flex: 1
				},{
					xtype: 'numberfield',
					fieldLabel: '单价 ',
					labelWidth: 60,
					hideTrigger: true,
					name: 'qdd_price',
					value: d.qdd_price,
					editable: true,
					flex: 1			
				}]
			});
		});
		fields.push({
			xtype: 'button',
			iconCls: 'x-button-icon-add',
			handler: function(b) {
				me.onStepAdd(b.ownerCt);
			}
		});
		return fields;
	},
	onStepAdd: function(form) {
		var fields = form.query('fieldcontainer');
		if(fields.length >= 10){
			showError('最多支持10个分段！');
		} else {
			form.insert(fields.length ,{
				xtype: 'fieldcontainer',
				layout: 'hbox',
				dataId: 0,
				items: [{
					xtype: 'numberfield',
					fieldLabel: '数量 ≥ ',
					labelWidth: 60,
					hideTrigger: true,
					name: 'qdd_lapqty',
					flex: 1
				},{
					xtype: 'numberfield',
					fieldLabel: '单价 ',
					labelWidth: 60,
					hideTrigger: true,
					name: 'qdd_price',
					flex: 1			
				}]
			});
			
		}
	},
	onStepConfirm: function(record, form, callback) {
		var dets = [], steps = [], err = [], items = form.query('fieldcontainer');
		Ext.each(items, function(container){
			var qtyField = container.down('field[name=qdd_lapqty]'),
				priceField = container.down('field[name=qdd_price]');
			if(qtyField && priceField && qtyField.value != null) {
				dets.push({qdd_lapqty: qtyField.value, qdd_id: container.dataId, qdd_price: priceField.value});
				if(steps.indexOf(qtyField.value) == -1)
					steps.push(qtyField.value);
				else
					err.push('数量：' + qtyField.value);
			}
		});
		if(err.length > 0) {
			showError('分段数量填写重复！<br>' + err.join('<br>'));
			return;
		}
		Ext.Array.sort(dets, function(a, b){
			return a.qdd_lapqty > b.qdd_lapqty;
		});
		record.set('dets', dets);
		record.dirty = true;
		record.modified = record.modified || {};
		record.modified['qd_lapqty'] = true;
		callback.call(null);
	},
	beforeSave: function(){
		var me = this;
		var form = Ext.getCmp('form'), id = Ext.getCmp(form.keyField).value;
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.isEmpty(id) || id == 0 || id == '0'){
			me.FormUtil.getSeqId(form);
		}
		var detail = Ext.getCmp('grid');
		var dets = [];
		detail.store.each(function(record, i){
			if(!me.GridUtil.isBlank(detail, record.data)) {
				if(record.get('qd_id') == null || record.get('qd_id') == 0){
					record.set('qd_id', -1 * i);
				}
				var s = record.get('dets') || [];
				Ext.Array.each(s, function(t, i){
					t.qdd_id = t.qdd_id || 0;
					t.qdd_qdid = String(record.get('qd_id'));
					dets.push(t);
				});
			}
		});
		me.FormUtil.beforeSave(me, Ext.encode(dets));
	},
	beforeUpdate: function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		var detail = Ext.getCmp('grid');
		var dets = [];
		detail.store.each(function(record, i){
			if(!me.GridUtil.isBlank(detail, record.data)) {
				if(record.get('qd_id') == null || record.get('qd_id') == 0){
					record.set('qd_id', -1 * i);
				}
				var s = record.get('dets') || [];				
				Ext.Array.each(s, function(t, i){
					t.qdd_id = t.qdd_id || 0;
					t.qdd_qdid = String(record.get('qd_id'));
					dets.push(t);
				});
			}
		});		
		me.FormUtil.onUpdate(form, false, null, Ext.encode(dets));
	},
	getStepWise: function(qu_id, callback) {
		Ext.Ajax.request({
			url: basePath + 'b2b/sale/zdquotation/det.action',
			params: {
				qu_id: qu_id
			},
			callback: function(opt, s, r) {
				if(s) {
					var rs = Ext.decode(r.responseText);
					callback.call(null, rs);
				}
			}
		});
	}
});