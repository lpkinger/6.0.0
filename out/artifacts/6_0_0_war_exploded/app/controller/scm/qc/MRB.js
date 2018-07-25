Ext.QuickTips.init();
Ext.define('erp.controller.scm.qc.MRB', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.qc.MRB','core.grid.Panel2','scm.qc.MRBDetailGrid','core.toolbar.Toolbar','core.form.MultiField','core.form.FileField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.ResAudit',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      		'core.button.ResSubmit','core.button.Flow','core.button.Check','core.button.ResCheck',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick,
    			afterrender: function(g) {
    				g.plugins[0].on('beforeedit', function(args){
    					if(g.readOnly) {
    						return false;
    					}
    					var status = args.record.data.md_statuscode, isok=args.record.data.md_isok,
    					isng = args.record.data.md_isng;
    					if(status == 'TURNIN') {
    						return false;
    					}
    					if(args.field == "md_okqty" && isok == 1) {
    						return false;
    					}
    					if(args.field == "md_ngqty" && isng == 1) {
    						return false;
    					}
    				});
    			},
    			reconfigure: function(grid) {
    				if (Ext.getCmp('mr_inqty')) {
    					var qty = Ext.getCmp('mr_inqty').value,
	    					record = grid.store.getAt(0);
	    				if(record.get('md_okqty') == 0 && record.get('md_ngqty') == 0
	    						&& record.get('md_id') == 0) {
	    					record.set('md_okqty', qty);
	    				}
    				}
    			}
    		},
    		'mrbdetail': { 
    			itemclick: this.onGridItemClick1
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				if(!me.isEqualQty()){
    					return;
    				}
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('mr_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: {	
	           		lock: 2000,
	                fn: function(btn){
	    				me.beforeUpdate();
	    			}
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addMRB', '新增MRB单', 'jsps/scm/qc/mrb.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mr_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('mr_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mr_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('mr_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mr_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('mr_id').value, true, me.beforeUpdate, me);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mr_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('mr_id').value);
    			}
    		},
    		'erpCheckButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mr_checkstatuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onCheck(Ext.getCmp('mr_id').value);
    			}
    		},
    		'erpResCheckButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mr_checkstatuscode');
    				if(status && status.value != 'APPROVE' ){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResCheck(Ext.getCmp('mr_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				var condition = '{QUA_MRB.mr_id}=' + Ext.getCmp('mr_id').value + '';
    				var id = Ext.getCmp('mr_id').value;
    				reportName="verifyMake";
    				me.FormUtil.onwindowsPrint(id, reportName, condition);
    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    onGridItemClick1: function(selModel, record){//grid行选择
    	this.gridLastSelected = record;
    	var grid = Ext.getCmp('MRBDetailGrid');
    	grid.lastSelectedRecord = record;
    	if(record.data[grid.necessaryField] == null || record.data[grid.necessaryField] == ''){
    		this.gridLastSelected.findable = true;//空数据可以在输入完code，并移开光标后，自动调出该条数据
    	} else {
    		this.gridLastSelected.findable = false;
    	}
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeUpdate: function(){
		var mm = this;
		if(! mm.FormUtil.checkForm()){
			return;
		}
		if(!mm.isEqualQty()){
    		return;
    	}
		var form=Ext.getCmp('form');
		var s1 = mm.FormUtil.checkFormDirty(form);
		var grid2 = Ext.getCmp('MRBDetailGrid');
		var grid = Ext.getCmp('grid'), qty = Ext.getCmp('mr_inqty').value,
			first = grid.store.first();
		var bool = true;
		if(first && first.get('md_okqty') == 0 && first.get('md_ngqty') == 0) {
			first.set('md_okqty', qty);
		}
		//合格数量不能大于送检数量
		grid.store.each(function(item){
			if(item.dirty){
				if(item.data['md_statuscode'] == "AUDITED"){
					bool = false;
					showError('明细表第' + item.data['md_detno'] + '行已审核，不能修改！');return;
				}
				if(item.data['md_okqty'] + item.data['md_ngqty'] > qty){
					bool = false;
					showError('明细表第' + item.data['md_detno'] + '行的合格数量与不合格数量之和不能大于送检数量！');return;	
				} else {
					item.set('md_checkqty', item.data['md_okqty'] + item.data['md_ngqty']);
				}
				item.set('md_samplingqty', item.data['md_samplingokqty'] + item.data['md_samplingngqty']);
				item.set('md_samplingngqtylv', item.data['md_samplingngqty']*100/item.data['md_samplingqty']);
			}
		});
		var sum = grid.store.getSum(grid.store.data.items, 'md_checkqty');
		if(sum > qty) {
			showError('送检数量之和不能大于收料数量!');return;
		}
		var param1 = mm.GridUtil.getGridStore(grid);
		var param2 = mm.GridUtil.getGridStore(grid2);
		//更新
		if(bool){
			if(s1 == '' && (param1 == null || param1 == '') && (param2 == null || param2 == '')){
				warnMsg('未添加或修改数据,是否继续?', function(btn){
					if(btn == 'yes'){
						mm.onUpdate(param1, param2);
					} else {
						return;
					}
				});
			} else {
				mm.onUpdate(param1, param2);
			}
		}
	},
	onUpdate:function(param1,param2){
		var me = this;
		var form = Ext.getCmp('form');
		param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
		param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
		if(form.getForm().isValid()){
			//form里面数据
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					//number类型赋默认值，不然sql无法执行
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
			//去除ignore字段
			var keys = Ext.Object.getKeys(r), f;
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
			});
			if(!me.FormUtil.contains(form.updateUrl, '?caller=', true)){
				form.updateUrl = form.updateUrl + "?caller=" + caller;
			}
			me.FormUtil.update(r, param1, param2);
		}else{
			me.FormUtil.checkForm();
		}
	},
	isEqualQty:function(){
		var inqty = Ext.getCmp('mr_inqty');
		if(inqty&&inqty.value!=''){
			var grid = Ext.getCmp('grid'),
			first = grid.store.first();
			var qty =0;
			if(first){
				grid.store.each(function(item){
					qty+=item.data['md_okqty']+item.data['md_ngqty'];
				});
				if(qty>inqty.value){
					showError('明细总数量大于来料数量，不允许操作!');
					return false;
				}				
			}
		}
		return true;
	}
});