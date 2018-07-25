Ext.QuickTips.init();
Ext.define('erp.controller.fa.gs.AccountCheck', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','fa.gs.AccountCheck','core.grid.Panel2','core.toolbar.Toolbar','core.form.ColorField','core.form.YnField',
    		 	'core.form.MonthDateField', 'core.form.ConDateField', 
    		'core.button.Scan','core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.ResSubmit',
    			'core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.grid.YnColumn'
    	],
    init:function(){
       var me=this;
    	this.control({ 
    		'erpGridPanel2': { 
    			afterrender: function(grid){
    				grid.plugins[0].on('beforeedit', function(args) {
                    	if (args.field == "acd_debit") {
                    		var bool = true;
                    		if (args.record.get('acd_credit') != null && args.record.get('acd_credit') > 0){
                    			bool = false;
                    		}
                    		return bool;
                        }
                        if (args.field == "acd_credit") {
                        	var bool = true;
                        	if (args.record.get('acd_debit') != null && args.record.get('acd_debit') > 0){
                    			bool = false;
                    		}
                    		return bool;
                        }
                    });
    			},
				itemclick: this.onGridItemClick
			},
			'field[name=acc_yearmonth]' : {
				afterrender: function(f) {
					if(Ext.isEmpty(f.value)){
						this.getMonth(f);
					}
				}
    		},
    	    'erpSaveButton': {
    	    	click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				//保存之前的一些前台的逻辑判定
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    				Ext.each(items,function(item,index){
    					if(!Ext.isEmpty(item.data['acd_catecode']) || !Ext.isEmpty(item.data['acd_explanation'])){
    						if((Ext.isEmpty(item.data['acd_debit']) || item.data['acd_debit'] == 0) && (Ext.isEmpty(item.data['acd_credit']) || item.data['acd_credit'] == 0)){
    							showError('明细表第' + item.data['acd_detno'] + '行借、贷方金额均未填写!');
    						}
    					}
    				});
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    		    afterrender: function(btn){
    				var status = Ext.getCmp('acc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    				Ext.each(items,function(item,index){
    					if(!Ext.isEmpty(item.data['acd_catecode']) || !Ext.isEmpty(item.data['acd_explanation'])){
    						if((Ext.isEmpty(item.data['acd_debit']) || item.data['acd_debit'] == 0) && (Ext.isEmpty(item.data['acd_credit']) || item.data['acd_credit'] == 0)){
    							showError('明细表第' + item.data['acd_detno'] + '行借、贷方金额均未填写!');
    						}
    					}
    				});
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    		  afterrender: function(btn){
    				var status = Ext.getCmp('acc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('acc_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addAccountCheck', '新增银行对账单', 'jsps/fa/gs/accountCheck.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('acc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    				Ext.each(items,function(item,index){
    					if(!Ext.isEmpty(item.data['acd_catecode']) || !Ext.isEmpty(item.data['acd_explanation'])){
    						if((Ext.isEmpty(item.data['acd_debit']) || item.data['acd_debit'] == 0) && (Ext.isEmpty(item.data['acd_credit']) || item.data['acd_credit'] == 0)){
    							showError('明细表第' + item.data['acd_detno'] + '行借、贷方金额均未填写!');
    							return;
    						}
    					}
    				});
    				me.FormUtil.onSubmit(Ext.getCmp('acc_id').value);
    			}
    		},
    		'erpResSubmitButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('acc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('acc_id').value);
    			}
    		
    		},
    	   'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('acc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('acc_id').value);
    			}
    		},   
    	   'erpResAuditButton':{
    	      afterrender: function(btn){
    				var status = Ext.getCmp('acc_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('acc_id').value);
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
	getMonth: function(f) {
    	Ext.Ajax.request({
    		url: basePath + 'fa/getMonth.action',
    		params: {
    			type: 'MONTH-B'
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				f.setValue(rs.data.PD_DETNO);
    			}
    		}
    	});
    }
});