Ext.QuickTips.init();
Ext.define('erp.controller.fa.fix.assetsCard', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.fix.assetsCard','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
      			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      			'core.button.ResSubmit','core.button.UpdateUseStatus',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.ColorField','core.button.CopyAll'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpCopyButton': {
    			click: function(btn) {
    				this.copy();
    			}
    		},
    		'field[name=ac_date]': {
    			beforerender : function(f) {
    				if(Ext.isEmpty(f.value)){
						me.getCurrentMonth(function(end){
							f.setValue(end);
						});
    				}
				}
    		},
    		'field[name=ac_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=ac_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'field[name=ac_usemonth]': {
    			change: function(f){
    				Ext.getCmp('ac_useyears').setValue(Ext.Number.toFixed(f.value/12, 8));
    			}
    		},
    		'field[name=ac_oldvalue]' : {
    			change: me.changetaxtotal
    		},
    		'field[name=ac_taxrate]' : {
    			change: me.changetaxtotal
    		},
    		'erpSaveButton': {
    			click: function(btn){
        			var form = me.getForm(btn), codeField = Ext.getCmp(form.codeField);
        			var oldvalue = Ext.getCmp('ac_oldvalue').value;
    				var useyear = Ext.getCmp('ac_useyears').value;
    				var crate = Ext.getCmp('ac_crate').value;
    				var kind = Ext.getCmp('ac_kindid');
    				if(codeField.value == null || codeField.value == ''){
    					if(kind){
							var res = me.getCode(kind.value);
							if(res != null && res != ''){
								codeField.setValue(res);
							} else {
								me.BaseUtil.getRandomNumber('AssetsCard',10,null);//自动添加编号
							}
						} else {
							me.BaseUtil.getRandomNumber(null,10,null);//自动添加编号
						}
    				}
    				if(oldvalue == null || oldvalue == '' || oldvalue == '0' || oldvalue == 0){
    					showError('原值不能为空或者为零!');
    					return;
    				}
    				if(useyear == null || useyear == '' || useyear == '0' || useyear == 0){
    					showError('使用年限不能为空或者为零!');
    					return;
    				}
    				/*if(crate == null || crate == '' || crate == '0' || crate == 0){
    					showError('净残值率不能为空或者为零!');
    					return;
    				}*/
    				me.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ac_id').value);
    			}
    		},
    		'field[name=ac_kindid]':{
    			change: function(f){
    				var res = me.getCode(f.value);
					if(res != null && res != ''){
						Ext.getCmp('ac_code').setValue(res);
					}
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var oldvalue = Ext.getCmp('ac_oldvalue').value;
    				var useyear = Ext.getCmp('ac_useyears').value;
    				var crate = Ext.getCmp('ac_crate').value;
    				if(oldvalue == null || oldvalue == '' || oldvalue == '0' || oldvalue == 0){
    					showError('原值不能为空或者为零!');
    					return;
    				}
    				if(useyear == null || useyear == '' || useyear == '0' || useyear == 0){
    					showError('使用年限不能为空或者为零!');
    					return;
    				}
    				me.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addAssetsCard', '新增固定资产卡片', 'jsps/fa/fix/assetsCard.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ac_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('ac_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ac_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('ac_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ac_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('ac_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ac_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ac_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				me.FormUtil.onPrint(Ext.getCmp('ac_id').value);
    			}
    		}
    	});
    }, 
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	/**
	 * 复制固定资产卡片
	 */
	copy: function(){
		var me = this, win = Ext.getCmp('copyAssetsCard-win');
		if(!win){
			var accode = Ext.getCmp('ac_code'), ackind = Ext.getCmp('ac_kind'), ackindid = Ext.getCmp('ac_kindid'),
			   	val1 = accode ? accode.value : '', val2 =  ackind ? ackind.value : '', val3 = ackindid ? ackindid.value : '';
			win = Ext.create('Ext.Window', {
				id: 'copyAssetsCard-win',
				title: '复制卡片 ' + val1,
				height: 200,
				width: 400,
				items: [{
					xtype: 'form',
					id: 'copyForm',
					height: '100%',
					width: '100%',
					bodyStyle: 'background:#f1f2f5;',
					items: [{
						margin: '10 0 0 0',
						xtype: 'textfield',
						fieldLabel: '卡片编号',
						name:'ac_newcode',
						allowBlank: false,
						value: val1
					},{
						margin: '10 0 0 0',
						xtype: 'dbfindtrigger',
						fieldLabel: '资产类别',
						name:'ac_newkind',
						allowBlank: false,
						value: val2,
						listeners:{
							aftertrigger:function(t, d){
								t.ownerCt.down('textfield[name=ac_newkindid]').setValue(d.get('ak_id'));
								t.ownerCt.down('textfield[name=ac_newkind]').setValue(d.get('ak_name'));
							}
						}
					},{
						margin: '3 0 0 0',
						xtype: 'textfield',
						name:'ac_newkindid',
						fieldLabel: '类型id',
						value: val3,
						allowBlank: false,
						hidden: true,
						readOnly:true
					}],
					closeAction: 'hide',
					buttonAlign: 'center',
					layout: {
						type: 'vbox',
						align: 'center'
					},
					buttons: [{
						text: $I18N.common.button.erpConfirmButton,
						cls: 'x-btn-blue',
						handler: function(btn) {
							var form = btn.ownerCt.ownerCt,
							a = form.down('textfield[name=ac_newcode]'),
							b = form.down('textfield[name=ac_newkindid]');
							me.copyAssetsCard(Ext.getCmp('ac_id').value, a.value, b.value);
						}
					}, {
						text: $I18N.common.button.erpCloseButton,
						cls: 'x-btn-blue',
						handler: function(btn) {
							btn.up('window').hide();
						}
					}]
				}]
			});
			me.BaseUtil.getSetting('AssetsCard', 'autoCode', function(bool) {
				win.down('textfield[name=ac_newcode]').setReadOnly(bool);
            });
			me.autoCode(function(code){
				win.down('textfield[name=ac_newcode]').setValue(code);
			});
		}
		win.show();
	},
	copyAssetsCard: function(acid, accode, kindid) {
		var me = this;
 	   	Ext.Ajax.request({
 		  url: basePath + 'fa/fix/copyAssetsCard.action',
 		  params: {
 			  caller: caller,
 			  id: acid,
 			  accode: accode,
 			  kindid: kindid
 		  },
 		  callback : function(options,success,response){
 			   me.FormUtil.getActiveTab().setLoading(false);
 			   var r = new Ext.decode(response.responseText);
 			   if(r.success){
 				   if(r.content){
 					  Ext.getCmp('copyAssetsCard-win').hide();
 	 				  showMessage('提示', '复制成功！<a href="javascript:openUrl(\'jsps/fa/fix/assetsCard.jsp?formCondition=ac_idIS' + 
 								 + r.content.ac_id + '\')">\n卡片ID:&lt;' + r.content.ac_id + '&gt;</a>');
 				   }
 			   } else {
 				  showError(r.exceptionInfo);
 			   }
 		   }
 	   	});
    },
	changetaxtotal: function(){
    	if(Ext.getCmp('ac_taxrate') && Ext.getCmp('ac_oldvalue')) {
    		var ac_taxrate = Ext.Number.from(Ext.getCmp('ac_taxrate').getValue(), 0);
    		var ac_oldvalue = Ext.Number.from(Ext.getCmp('ac_oldvalue').getValue(), 0);
    		console.log(ac_taxrate);
    		Ext.getCmp('ac_taxtotal').setValue(Ext.Number.toFixed(ac_oldvalue*(1+ac_taxrate/100), 2));
    	}
    },
    getCurrentMonth : function(callback) {
		var me = this;
		Ext.Ajax.request({
			url : basePath + 'fa/getMonth.action',
			params : {
				type : 'MONTH-F'
			},
			callback : function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if (rs.data) {
					me.currentMonth = rs.data.PD_DETNO;
					me.datestart = Ext.Date.format(new Date(rs.data.PD_STARTDATE), 'Ymd');
					me.dateend = Ext.Date.format(new Date(rs.data.PD_ENDDATE), 'Y-m-d');
					callback.call(null, me.dateend);
				}
			}
		});
	},
	getCode: function(kind) {
		var result = null;
		Ext.Ajax.request({
	   		url : basePath + 'fa/fix/getAssetsCardCodeNum.action',
	   		async: false,
	   		params: {
	   			caller: caller,
	   			kind: kind
	   		},
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
				if(r.codes) {
					result = r.codes.code;
					Ext.getCmp('ac_number').setValue(r.codes.number);
				} else {
					showError(res.exceptionInfo);
				}
	   		}
		});
		return result;
	},
	autoCode : function(callback) {
		var form = Ext.getCmp('copyForm');
		var res = this.getCode(form.down('textfield[name=ac_newkindid]').value);
		if(res != null && res != ''){
			callback.call(null, res);
		}
	}
});