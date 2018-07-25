Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.ProductReview', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.bom.ProductReview','core.form.Panel','core.grid.Panel2','core.form.MultiField','core.form.FileField',
    		'core.button.Add','core.button.Save','core.button.Close',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
    		'core.button.Update','core.button.Delete','core.form.YnField','core.grid.YnColumn',
    		'core.toolbar.Toolbar','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger',
    		'core.trigger.MultiDbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			afterrender: function(g) {
                    g.plugins[0].on('beforeedit', function(args) {
                    	if (args.field == "pvd_prodcode") {
                    		var bool = true;
                    		if (args.record.get('pvd_isstandard') != null && args.record.get('pvd_isstandard') == 1){
                    			bool = false;
                    		}
                    		return bool;
                        }
                        if (args.field == "pvd_fpcode") {
                        	var bool = true;
                        	if (args.record.get('pvd_isstandard') != null && args.record.get('pvd_isstandard') != 1){
                    			bool = false;
                    		}
                    		return bool;
                        }
                        if (args.field == "pvd_fbcode") {
                        	var bool = true;
                        	if (args.record.get('pvd_isstandard') != null && args.record.get('pvd_isstandard') != 2){
                    			bool = false;
                    		}
                    		return bool;
                        }
                    });
    			},
    			itemclick: function(selModel, record){
    				if(!Ext.isEmpty(record.data.pvd_id) && !Ext.isEmpty(record.data.pvd_ftcode)){
    					var btn = Ext.getCmp('needSpec');
    					btn && btn.setDisabled(false);
    				}
    				this.onGridItemClick(selModel, record);
    			}
			},
			'gridcolumn[dataIndex=pvd_prodcode]': {
				afterrender: function(column) {
					column.renderer = function(val, meta, record) {
						var standard = record.get('pvd_isstandard');
						if(!val && (standard == 0 || standard == 2)) {
							meta.tdCls = 'x-form-necessary';
						} else {
							meta.tdCls = null;
						}
						return val;
					}
				}
			},
			'gridcolumn[dataIndex=pvd_fbcode]': {
				afterrender: function(column) {
					column.renderer = function(val, meta, record) {
						var standard = record.get('pvd_isstandard');
						if(!val && standard == 2) {
							meta.tdCls = 'x-form-necessary';
						} else {
							meta.tdCls = null;
						}
						return val;
					}
				}
			},
			'gridcolumn[dataIndex=pvd_fpcode]': {
				afterrender: function(column) {
					column.renderer = function(val, meta, record) {
						var standard = record.get('pvd_isstandard');
						if(!val && standard == 1 ) {
							meta.tdCls = 'x-form-necessary';
						} else {
							meta.tdCls = null;
						}
						return val;
					}
				}
			},
			'gridcolumn[dataIndex=pvd_ftcode]': {
				afterrender: function(column) {
					column.renderer = function(val, meta, record) {
						var standard = record.get('pvd_isstandard');
						if(!val && standard == 1 ) {
							meta.tdCls = 'x-form-necessary';
						} else {
							meta.tdCls = null;
						}
						return val;
					}
				}
			},
			/**
    		 * 产品需求参数设置
    		 */
    		'#needSpec': {
    			click: function(btn) {
    				var record=btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();    				    	
    			    me.needSpec(record);
    			}
    		},
    		'field[name=pvd_fpcode]': {
    			focus: function(f){
    				var grid = Ext.getCmp('grid');
					var record = grid.selModel.lastSelected;
					me.featureView(record);
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
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
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('pv_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addProductReview', '新增产品评审', 'jsps/pm/bom/productReview.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pv_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('pv_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pv_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('pv_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pv_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('pv_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pv_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('pv_id').value);
    			}
    		},
    		'dbfindtrigger[name=pvd_fpcode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var code = record.data['pvd_ftcode'];
    				if(!Ext.isEmpty(code)){
    					t.dbBaseCondition = "fp_ftcode='" + code + "'";
    				}
    			}
    		},
    		'dbfindtrigger[name=pvd_prodcode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var code = record.data['pvd_ftcode'];
    				if(!Ext.isEmpty(code)){
    					t.dbBaseCondition = "pr_refno='" + code + "'";
    				}
    			}
    		}
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	/**
	 *产品参数维护
	 *
	 **/
	needSpec:function(record){
		var width = Ext.isIE ? screen.width*0.7*0.9 : '80%',
		   	height = Ext.isIE ? screen.height*0.75 : '80%';
		var pvd_id = record.get('pvd_id'), pvd_prodcode = record.get('pvd_prodcode');
		if(!Ext.isEmpty(pvd_prodcode)){
			showError("填写物料编号的不需要重新设置需求参数！");
			return;
		}
		Ext.create('Ext.Window', {
			width: width,
			height: height,
			autoShow: true,
			layout: 'anchor',
			items: [{
				tag : 'iframe',
				frame : true,
				anchor : '100% 100%',
				layout : 'fit',
				html : '<iframe id="iframe_dbfind" src="' + basePath + 'jsps/pm/bom/needFeature.jsp?formCondition=pvd_id=' 
					+ pvd_id + '&gridCondition=nf_pvdid=' + pvd_id + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			}]
		});
	},
	
	/**
	 *标准库号选择
	 *
	 **/
	featureView:function(gridrecord){
		var width = Ext.isIE ? screen.width*0.7*0.9 : '80%',
			height = Ext.isIE ? screen.height*0.75 : '80%';
		var ftcode = gridrecord.data['pvd_ftcode'], ftname = gridrecord.data['ft_name'], fpcode = gridrecord.data['pvd_fpcode'];
		if(Ext.isEmpty(ftcode)){
			showError("请先填写模板编号！");
			return;
		}
		var win = Ext.create('Ext.Window', {
			width: width,
			height: height,
			autoShow: true,
			layout: 'anchor',
			items: [{
				tag : 'iframe',
				frame : true,
				anchor : '100% 100%',
				layout : 'fit',
				html : '<iframe src="' + basePath + 'jsps/common/deallist.jsp?whoami=FeatureView!Query&ft_code=' 
						+ ftcode + '&ft_name=' + ftname + '&fp_code=' + fpcode + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			}]
		});
		var fpcode = null;
		var fpspec = null;
		Ext.defer(function(){
			var grid = win.getEl().down('iframe').dom.contentWindow.document.defaultView.Ext.getCmp('grid');
			if (grid) {
				grid.on('itemmousedown', function(selModel, record){
					fpcode = record.data['fv_fpcode'];
					fpspec = record.data['fp_description2'];
					if(fpcode != null){
						gridrecord.set('pvd_fpcode',fpcode);
					}
					if(fpspec != null){
						gridrecord.set('pvd_needspec',fpspec);
					}
					win.close();
				});
			}
		}, 2000);
	}
});