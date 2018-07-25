Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.ProdChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.product.ProdChange','core.grid.Panel2','core.toolbar.Toolbar','core.form.FileField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
  			'core.button.ResSubmit','core.form.FileField', 'core.form.MultiField','core.trigger.MultiDbfindTrigger','core.window.SelectInquiryDate',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.grid.YnColumnNV'
  	],
  	selectInquiryDate_rowIndex:0,
    selectInquiryDate_count:0,
	init:function(){
		var me = this;
		me.alloweditor = true;
		this.control({
			'erpGridPanel2': { 
				itemclick: this.onGridItemClick,
				cellclick: function(selModel, td, cellIndex, record, tr, rowIndex, e, eOpts) {
    				//点击的是静态询价周期，打开窗口
    				if(selModel.getGridColumns()[cellIndex]){
	    				if(selModel.getGridColumns()[cellIndex].dataIndex=='pcd_newjtcycle'){
	    					//添加行和周期在变量中
	    					me.selectInquiryDate_rowIndex = rowIndex;
	    					me.selectInquiryDate_count = record.get('pcd_newjtcycle');
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
					var grid = Ext.getCmp('grid'), items = grid.store.data.items,
						bool = true;
					if(bool)
						this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('pc_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					var grid = Ext.getCmp('grid'), items = grid.store.data.items,
						bool = true;
					if(bool)
						this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addProdChange', '新增物料种类变更单', 'jsps/scm/product/prodChange.jsp?whoami=' + caller);
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pc_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.onSubmit(Ext.getCmp('pc_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('pc_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('pc_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pc_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('pc_id').value);
				}
			},
			'erpPrintButton': {
				click:function(btn){
					var reportName="ProdChange";
					var condition='{ProdChange.pc_id}='+Ext.getCmp('pc_id').value+'';
					var id=Ext.getCmp('pc_id').value;
					me.FormUtil.onwindowsPrint2(id,reportName,condition);
				}
			},
			'combo':{
    			focus:function(combo,the){
    				if(combo.name=='pcd_newjtcycle'){  //点击从表的静态询价周期触发
    					Ext.create('erp.view.core.window.SelectInquiryDate',{rowIndex:me.selectInquiryDate_rowIndex,count:me.selectInquiryDate_count});
    				}
    			}
    		}
		});
	}, 
	/**
	 * @param allowEmpty 是否允许Grid为空
	 */
	onSubmit: function(id, allowEmpty, errFn, scope){
		var me = this;
		var form = Ext.getCmp('form');
		if(form && form.getForm().isValid()){
			var s = me.FormUtil.checkFormDirty(form);
			if(s == '' || s == '<br/>'){
				me.FormUtil.submit(id);
			} else {
				Ext.MessageBox.show({
				     title:'保存修改?',
				     msg: '该单据已被修改:<br/>' + s + '<br/>提交前要先保存吗？',
				     buttons: Ext.Msg.YESNOCANCEL,
				     icon: Ext.Msg.WARNING,
				     fn: function(btn){
				    	 if(btn == 'yes'){
				    		 if(typeof errFn === 'function')
				    			 errFn.call(scope);
				    		 else
				    			 me.FormUtil.onUpdate(form, true);
				    	 } else if(btn == 'no'){
				    		 me.FormUtil.submit(id);	
				    	 } else {
				    		 return;
				    	 }
				     }
				});
			}
		} else {
			me.FormUtil.checkForm();
		}
	},
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});