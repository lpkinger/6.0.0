Ext.QuickTips.init();
Ext.define('erp.controller.fa.gs.BillAP', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.gs.BillAP','core.form.Panel','core.form.MultiField','core.form.FileField','core.form.MultiField',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close',
    		'core.button.ResSubmit','core.button.Update','core.button.Delete','core.button.Nullify',
    		'core.button.ResAudit','core.button.UpdateInfo','core.window.AssWindow','core.button.AssMain',
    		'core.button.CopyAll',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.form.SeparNumber'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'field[name=bap_duedate]':{
				beforerender: function(field){
					field.readOnly=false;
				}
			},
			'field[name=bap_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=bap_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'field[name=bap_vendcode]':{
				beforerender: function(field){
					if(Ext.getCmp('bap_paybillcode')&& !Ext.isEmpty(Ext.getCmp('bap_paybillcode').value)){
						field.readOnly=true;
					}
				}
			},
			'field[name=bap_billkind]':{
				beforerender: function(field){
					if(Ext.getCmp('bap_paybillcode')&& !Ext.isEmpty(Ext.getCmp('bap_paybillcode').value)){
						field.readOnly=true;
					}
				}
			},
			'field[name=ca_asstype]':{
    			change: function(f){
    				var btn = Ext.getCmp('assmainbutton');
    				if(Ext.getCmp('bap_billkind').value != '其他付款'){
    					btn.hide();
    				} else {
    					btn && btn.setDisabled(Ext.isEmpty(f.value));
    				}
    			}
    		},
			'erpAssMainButton':{
    			afterrender:function(btn){
    				if(Ext.getCmp('bap_billkind').value != '其他付款'){
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
    				if(Ext.Date.format(Ext.getCmp('bap_duedate').value,'Y-m-d') < Ext.Date.format(Ext.getCmp('bap_date').value,'Y-m-d')){
    					showError('到期日期小于票据日期'); return;
    				}
    				if(Ext.Date.format(Ext.getCmp('bap_outdate').value,'Y-m-d') > Ext.Date.format(Ext.getCmp('bap_duedate').value,'Y-m-d')){
    					showError('出票日期大于到期日期'); return;
    				}
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();
    				}
    				if(Ext.getCmp('bap_billkind').value == '其他付款'){
    					if(Ext.isEmpty(Ext.getCmp('bap_feecatecode').value)){
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
    				me.FormUtil.onAdd('addBillAP', '新增应付票据', 'jsps/fa/gs/billAP.jsp');
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.Date.format(Ext.getCmp('bap_duedate').value,'Y-m-d') < Ext.Date.format(Ext.getCmp('bap_date').value,'Y-m-d')){
    					showError('到期日期小于票据日期'); return;
    				}
    				if(Ext.Date.format(Ext.getCmp('bap_outdate').value,'Y-m-d') > Ext.Date.format(Ext.getCmp('bap_duedate').value,'Y-m-d')){
    					showError('出票日期大于到期日期'); return;
    				}
    				if(Ext.getCmp('bap_billkind').value == '其他付款'){
    					if(Ext.isEmpty(Ext.getCmp('bap_feecatecode').value)){
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
    				this.FormUtil.onDelete(Ext.getCmp('bap_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bap_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				if(Ext.Date.format(Ext.getCmp('bap_duedate').value,'Y-m-d') < Ext.Date.format(Ext.getCmp('bap_date').value,'Y-m-d')){
    					showError('到期日期小于票据日期'); return;
    				}
    				if(Ext.Date.format(Ext.getCmp('bap_outdate').value,'Y-m-d') > Ext.Date.format(Ext.getCmp('bap_duedate').value,'Y-m-d')){
    					showError('出票日期大于到期日期'); return;
    				}
    				if(Ext.getCmp('bap_billkind').value == '其他付款'){
    					if(Ext.isEmpty(Ext.getCmp('bap_feecatecode').value)){
    						showError('费用科目必须填写！'); return;
    					}
    				}
    				var cmcurrency = Ext.getCmp('bap_cmcurrency').getValue();
    				var currency = Ext.getCmp('bap_currency').getValue();
    				var cmrate = Number(Ext.getCmp('bap_cmrate').getValue());
    				if(currency == cmcurrency){
    					if(cmrate != 1){
    						showError('币别相同，冲账汇率不等于1,不能提交!');
    						return;
    					}
    				}
    	    		if(currency != cmcurrency){
    					if(cmrate == 1){
    						showError('币别不相同，冲账汇率等于1,不能提交!');
    						return;
    					}
    				}
    				me.FormUtil.onSubmit(Ext.getCmp('bap_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bap_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('bap_id').value);
    			}
    		},
    		'erpAuditButton':{
				afterrender: function(btn){
    				var status = Ext.getCmp('bap_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: {
    				fn:function(btn){
        				me.auditBillAP();
        			},
        			lock:2000
    			}
			},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bap_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('bap_id').value);
    			}
    		},
    		'erpNullifyButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bap_nowstatus');
    				if(status && status.value != '已作废'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onNullify(Ext.getCmp('bap_id').value);
    			}
    		},
    		'textfield[name=bap_cmrate]':{
    			beforerender: function(field){
					if(Ext.getCmp('bap_paybillcode') && !Ext.isEmpty(Ext.getCmp('bap_paybillcode').value)){
						field.readOnly=true;
					}
    			},
    			change: me.gettopaybalance
    		},
    		'textfield[name=bap_doublebalance]':{
    			change: me.gettopaybalance
    		},
    		'field[name=bap_cmcurrency]':{
    			beforerender: function(field){
					if(Ext.getCmp('bap_paybillcode') && !Ext.isEmpty(Ext.getCmp('bap_paybillcode').value)){
						field.readOnly=true;
					}
					if(Ext.getCmp('bap_sourcetype') && Ext.getCmp('bap_sourcetype').value == '总务申请单'){
						field.readOnly=true;
					}
    			}
    		},
    		'field[name=bap_topaybalance]':{
    			beforerender: function(field){
    				if(Ext.getCmp('bap_paybillcode') && Ext.isEmpty(Ext.getCmp('bap_paybillcode').value)){
						field.readOnly=true;
					}
    			},
    			change: me.gettopaybalance
    		},
    		'erpUpdateInfoButton':{
    			click:function(){
    				var text=Ext.getCmp('bap_duedate');
    					me.updateInfo(text.value,Ext.getCmp('bap_id').value);
    			}
    		},
    		'erpCopyButton': {
    			click: function(btn) {
    				this.copy();
    			}
    		}
    	});
    },
    updateInfo:function(text,id){
		Ext.Ajax.request({
        	url : basePath + 'fa/gs/billap/updateInfo.action',
        	params: {text:text,id:id,caller:caller},
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
	auditBillAP:function(){
		var me = this;
		if(Ext.Date.format(Ext.getCmp('bap_duedate').value,'Y-m-d') < Ext.Date.format(Ext.getCmp('bap_date').value,'Y-m-d')){
			showError('到期日期小于票据日期'); return;
		}
		if(Ext.Date.format(Ext.getCmp('bap_outdate').value,'Y-m-d') > Ext.Date.format(Ext.getCmp('bap_duedate').value,'Y-m-d')){
			showError('出票日期大于到期日期'); return;
		}
		var cmcurrency = Ext.getCmp('bap_cmcurrency').getValue();
		var currency = Ext.getCmp('bap_currency').getValue();
		var cmrate = Number(Ext.getCmp('bap_cmrate').getValue());
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
		me.FormUtil.onAudit(Ext.getCmp('bap_id').value);
	},
	copy: function(){
	 	var me = this;
		var form = Ext.getCmp('form');
		var v = form.down('#bap_id').value;
		if(v > 0) {
			form.setLoading(true);
			Ext.Ajax.request({
				url: basePath + 'fa/gs/copyBillAP.action',
				params: {
					id: v
				},
				callback: function(opt, s, r){
					form.setLoading(false);
					var res = Ext.decode(r.responseText);
					if(res.ar) {
						turnSuccess(function(){
	    					var id = res.ar.bap_id;
	    					var url = "jsps/fa/gs/billAP.jsp?formCondition=bap_idIS"+ id ;
	    					me.FormUtil.onAdd('billAP' + id, '应付票据' + id, url);
	    				});
					} else {
						showError(res.exceptionInfo);
					}
				}
			});
		}
	},
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	gettopaybalance: function(){
		if(Ext.isEmpty(Ext.getCmp('bap_paybillcode').value)){
			var doublebalance = Ext.Number.from(Ext.getCmp('bap_doublebalance').getValue(), 0);
	    	var cmrate = Ext.Number.from(Ext.getCmp('bap_cmrate').getValue(), 0);
	    	Ext.getCmp('bap_topaybalance').setValue(Ext.Number.toFixed(doublebalance*cmrate, 2));
	    	if (typeof (f = Ext.getCmp('bap_leftamount')) != 'undefined' ) {
        		var v1 = (Ext.getCmp('bap_settleamount').value || 0);
        		Ext.getCmp('bap_leftamount').setValue(Ext.Number.toFixed(doublebalance-v1, 2));
        	}
		} else if(!Ext.isEmpty(Ext.getCmp('bap_paybillcode').value)) {
			var doublebalance = Ext.Number.from(Ext.getCmp('bap_doublebalance').getValue(), 0);
		    var topaybalance = Ext.Number.from(Ext.getCmp('bap_topaybalance').getValue(), 0);
		    Ext.getCmp('bap_cmrate').setValue(Ext.Number.toFixed(topaybalance/doublebalance, 8));
		    if (typeof (f = Ext.getCmp('bap_leftamount')) != 'undefined' ) {
	        	var v1 = (Ext.getCmp('bap_settleamount').value || 0);
	        	Ext.getCmp('bap_leftamount').setValue(Ext.Number.toFixed(doublebalance-v1, 2));
	        }
		}
	}
});