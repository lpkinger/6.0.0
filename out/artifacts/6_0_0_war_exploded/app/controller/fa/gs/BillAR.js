Ext.QuickTips.init();
Ext.define('erp.controller.fa.gs.BillAR', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.gs.BillAR','core.form.Panel','core.form.MultiField','core.form.FileField','core.form.MultiField',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close',
    		'core.button.ResSubmit','core.button.Update','core.button.Delete','core.button.Nullify','core.button.CopyAll',
    		'core.button.ResAudit','core.button.TurnRecBalance','core.button.Accounted','core.button.ResAccounted',
    		'core.button.UpdateInfo','core.window.AssWindow','core.button.AssMain','core.button.Split',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.form.SeparNumber'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'field[name=bar_duedate]':{
				beforerender: function(field){
					field.readOnly=false;
				}
			},
			'field[name=bar_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=bar_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'field[name=ca_asstype]':{
    			change: function(f){
    				var btn = Ext.getCmp('assmainbutton');
    				if(Ext.getCmp('bar_billkind').value != '其他收款'){
    					btn.hide();
    				} else {
    					btn && btn.setDisabled(Ext.isEmpty(f.value));
    				}
    			}
    		},
    		'erpAssMainButton':{
    			afterrender:function(btn){
    				if(Ext.getCmp('bar_billkind').value != '其他收款'){
    					btn.hide();
    				} else {
    					if(Ext.getCmp('ca_asstype') && Ext.isEmpty(Ext.getCmp('ca_asstype').getValue())){
        					btn.setDisabled(true);
        				} else {
        					btn.setDisabled(false);
        				}
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				if(Ext.getCmp('bar_duedate').value < Ext.getCmp('bar_date').value){
    					showError('到期日期小于票据日期'); return;
    				}
    				if(Ext.getCmp('bar_outdate').value > Ext.getCmp('bar_duedate').value){
    					showError('出票日期大于到期日期'); return;
    				}
    				if(Ext.getCmp('bar_billkind').value == '其他收款'){
    					if(Ext.isEmpty(Ext.getCmp('bar_feecatecode').value)){
    						showError('费用科目必须填写！'); return;
    					}
    				}
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();
    				}
    				var param = new Array();
    				if(Ext.getCmp('assmainbutton')){
    					Ext.each(Ext.Object.getKeys(Ext.getCmp('assmainbutton').cacheStoreForm), function(key){
    						Ext.each(Ext.getCmp('assmainbutton').cacheStoreForm[key], function(d){
    							d['ass_conid'] = key;
    							param.push(d);
    						});
    					});	
    				}
    				if(Ext.isEmpty(me.FormUtil.checkFormDirty(form)) && param.length == 0){
    					showError($I18N.common.grid.emptyDetail);
    					return;
    				} else {
    					param = param == null ? [] : Ext.encode(param).replace(/\\/g,"%");
    					if(form.getForm().isValid()){
    						Ext.each(form.items.items, function(item){
    							if(item.xtype == 'numberfield'){
    								if(item.value == null || item.value == ''){
    									item.setValue(0);
    								}
    							}
    						});
    						var r = form.getValues();
    						form.getForm().getFields().each(function(){
    							if(this.logic == 'ignore') {
    								delete r[this.name];
    							}
    						});
    						me.FormUtil.beforeSave(r, param);
    					}else{
    						me.FormUtil.checkForm();
    					}
    				}
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addBillAR', '新增应收票据', 'jsps/fa/gs/billAR.jsp');
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp('bar_duedate').value < Ext.getCmp('bar_date').value){
    					showError('到期日期小于票据日期'); return;
    				}
    				if(Ext.getCmp('bar_outdate').value > Ext.getCmp('bar_duedate').value){
    					showError('出票日期大于到期日期'); return;
    				}
    				if(Ext.getCmp('bar_billkind').value == '其他收款'){
    					if(Ext.isEmpty(Ext.getCmp('bar_feecatecode').value)){
    						showError('费用科目必须填写！'); return;
    					}
    				}
    				var param = new Array();
    				if(Ext.getCmp('assmainbutton')){
    					Ext.each(Ext.Object.getKeys(Ext.getCmp('assmainbutton').cacheStoreForm), function(key){
    						Ext.each(Ext.getCmp('assmainbutton').cacheStoreForm[key], function(d){
    							d['ass_conid'] = key;
    							param.push(d);
    						});
    					});	
    				}
    				if(Ext.isEmpty(me.FormUtil.checkFormDirty(form)) && param.length == 0){
    					showError($I18N.common.grid.emptyDetail);
    					return;
    				} else {
    					param = param == null ? [] : Ext.encode(param).replace(/\\/g,"%");
    					if(form.getForm().isValid()){
    						Ext.each(form.items.items, function(item){
    							if(item.xtype == 'numberfield'){
    								if(item.value == null || item.value == ''){
    									item.setValue(0);
    								}
    							}
    						});
    						var r = form.getValues();
    						form.getForm().getFields().each(function(){
    							if(this.logic == 'ignore') {
    								delete r[this.name];
    							}
    						});
    						me.FormUtil.update(r, param);
    					}else{
    						me.FormUtil.checkForm();
    					}
    				}
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('bar_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bar_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				if(Ext.getCmp('bar_duedate').value < Ext.getCmp('bar_date').value){
    					showError('到期日期小于票据日期'); return;
    				}
    				if(Ext.getCmp('bar_outdate').value > Ext.getCmp('bar_duedate').value){
    					showError('出票日期大于到期日期'); return;
    				}
    				if(Ext.getCmp('bar_billkind').value == '其他收款'){
    					if(Ext.isEmpty(Ext.getCmp('bar_feecatecode').value)){
    						showError('费用科目必须填写！'); return;
    					}
    				}
    				var cmcurrency = Ext.getCmp('bar_cmcurrency').getValue();
    				var currency = Ext.getCmp('bar_currency').getValue();
    				var cmrate = Number(Ext.getCmp('bar_cmrate').getValue());
    				if(currency == cmcurrency){
    					if(cmrate != 1){
    						showError('币别相同，冲账汇率不等于1,不能过账!');
    						return;
    					}
    				}
    	    		if(currency != cmcurrency){
    					if(cmrate == 1){
    						showError('币别不相同，冲账汇率等于1,不能过账!');
    						return;
    					}
    				}
    				me.FormUtil.onSubmit(Ext.getCmp('bar_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bar_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('bar_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bar_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: {
    				fn:function(btn){
        				me.auditBillAR();
        			},
        			lock:2000
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bar_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('bar_id').value);
    			}
    		},
    		'erpAccountedButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bar_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				if(Ext.getCmp('bar_duedate').value < Ext.getCmp('bar_date').value){
    					showError('到期日期小于票据日期'); return;
    				}
    				if(Ext.getCmp('bar_outdate').value > Ext.getCmp('bar_duedate').value){
    					showError('出票日期大于到期日期'); return;
    				}
    				me.FormUtil.onAccounted(Ext.getCmp('bar_id').value);
    			}
    		},
    		'erpResAccountedButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bar_statuscode');
    				if(status && status.value != 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAccounted(Ext.getCmp('bar_id').value);
    			}
    		},
    		'erpSplitButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('bar_statuscode'), leftamount = Ext.getCmp('bar_leftamount');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                    if(leftamount && leftamount == 0){
                    	btn.hide();
                    }
                },
                click: function(btn) {
                	me.splitBillAR();
                }
            },
    		'erpNullifyButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bar_nowstatus');
    				if(status && status.value != '已作废'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onNullify(Ext.getCmp('bar_id').value);
    			}
    		},
    		'erpCopyButton': {
    			click: function(btn) {
    				this.copy();
    			}
    		},
    		'textfield[name=bar_cmrate]':{
    			change: me.gettopaybalance
    		},
    		'textfield[name=bar_doublebalance]':{
    			change: me.gettopaybalance
    		},
    		'textfield[name=bar_topaybalance]':{
    			change: me.getcmrate
    		},
    		'erpUpdateInfoButton':{
    			click:function(){
    				var text=Ext.getCmp('bar_duedate');
    					me.updateInfo(text.value,Ext.getCmp('bar_id').value);
    			}
    		}
    	});
    },
    gettopaybalance: function(){
    	if(Ext.getCmp('bar_doublebalance') && Ext.getCmp('bar_cmrate')) {
    		var doublebalance = Ext.Number.from(Ext.getCmp('bar_doublebalance').getValue(), 0);
    		var cmrate = Ext.Number.from(Ext.getCmp('bar_cmrate').getValue(), 0);
    		Ext.getCmp('bar_topaybalance').setValue(Ext.Number.toFixed(doublebalance*cmrate, 2));
    		if (typeof (f = Ext.getCmp('bar_leftamount')) != 'undefined' ) {
        		var v1 = (Ext.getCmp('bar_settleamount').value || 0);
        		Ext.getCmp('bar_leftamount').setValue(Ext.Number.toFixed(doublebalance-v1, 2));
        	}
    	}
    },
    getcmrate: function(){
    	if(Ext.getCmp('bar_doublebalance') && Ext.getCmp('bar_topaybalance')) {
    		var doublebalance = Ext.Number.from(Ext.getCmp('bar_doublebalance').getValue(), 0);
    		var topaybalance = Ext.Number.from(Ext.getCmp('bar_topaybalance').getValue(), 0);
    		if(doublebalance != 0){
    			Ext.getCmp('bar_cmrate').setValue(Ext.Number.toFixed(topaybalance/doublebalance, 15));
    		}
    	}
    },
    updateInfo:function(text,id){
		Ext.Ajax.request({
        	url : basePath + 'fa/gs/billar/updateInfo.action',
        	params: {text:text,id:id},
        	method : 'post',
        	async:false,
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		}
        		showMessage("提示", '更新成功！');
        		window.location.reload();
        	}
        });
	},
	auditBillAR:function(){
		var me = this;
		if(Ext.getCmp('bar_duedate').value < Ext.getCmp('bar_date').value){
			showError('到期日期小于票据日期'); return;
		}
		if(Ext.getCmp('bar_outdate').value > Ext.getCmp('bar_duedate').value){
			showError('出票日期大于到期日期'); return;
		}
		var cmcurrency = Ext.getCmp('bar_cmcurrency').getValue();
		var currency = Ext.getCmp('bar_currency').getValue();
		var cmrate = Number(Ext.getCmp('bar_cmrate').getValue());
		if(currency == cmcurrency){
			if(cmrate != 1){
				showError('币别相同，冲账汇率不等于1,不能过账!');
				return;
			}
		}
		if(currency != cmcurrency){
			if(cmrate == 1){
				showError('币别不相同，冲账汇率等于1,不能过账!');
				return;
			}
		}
		me.FormUtil.onAudit(Ext.getCmp('bar_id').value);
	},
    copy: function(){
	 	var me = this;
		var form = Ext.getCmp('form');
		var v = form.down('#bar_id').value;
		if(v > 0) {
			form.setLoading(true);
			Ext.Ajax.request({
				url: basePath + 'fa/gs/copyBillAR.action',
				params: {
					id: v
				},
				callback: function(opt, s, r){
					form.setLoading(false);
					var res = Ext.decode(r.responseText);
					if(res.ar) {
						turnSuccess(function(){
	    					var id = res.ar.bar_id;
	    					var url = "jsps/fa/gs/billAR.jsp?formCondition=bar_idIS"+ id ;
	    					me.FormUtil.onAdd('billAR' + id, '应收票据' + id, url);
	    				});
					} else {
						showError(res.exceptionInfo);
					}
				}
			});
		}
	},
	splitBillAR: function() {
        var width = Ext.isIE ? screen.width * 0.7 * 0.9 : '80%',
            height = Ext.isIE ? screen.height * 0.75 : '100%';
        var bar_id = Ext.getCmp('bar_id').value;
        Ext.create('Ext.Window', {
            width: width,
            height: height,
            autoShow: true,
            layout: 'anchor',
            items: [{
                tag: 'iframe',
                frame: true,
                anchor: '100% 100%',
                layout: 'fit',
                html: '<iframe id="iframe_dbfind" src="' + basePath + 'jsps/fa/gs/billARSplit.jsp?formCondition=bar_id=' +
                bar_id + '&gridCondition=brs_barid=' + bar_id + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
            }]
        });
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});