Ext.QuickTips.init();
Ext.define('erp.controller.fa.gs.BillARChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.gs.BillARChange','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.ResAudit',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      		'core.button.ResSubmit','core.button.Flow','core.trigger.MultiDbfindTrigger','core.button.Accounted','core.button.ResAccounted',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.SeparNumber',
			'core.window.AssWindow', 'core.button.AssDetail','core.button.AssMain'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'field[name=ca_asstype]':{
    			change: function(f){
    				var btn = Ext.getCmp('assmainbutton');
    				if(!Ext.Array.contains(['贴现','其他收款','拆分'],Ext.getCmp('brc_kind').value)){
    					btn.hide();
    				} else if(Ext.getCmp('brc_kind').value=='其他收款' || Ext.getCmp('brc_kind').value=='拆分'){
						btn.show();
						btn && btn.setDisabled(Ext.isEmpty(f.value));
    				}
    			}
    		},
    		'field[name=brc_feecatecode]':{
    			change: function(f){
    				if(Ext.getCmp('brc_kind').value=='贴现'){
    					me.getFeeCate();
    				}
    			}
    		},
    		'erpAssMainButton':{
    			afterrender:function(btn){
    				if(!Ext.Array.contains(['贴现','其他收款','拆分'],Ext.getCmp('brc_kind').value)){
    					btn.hide();
    				} else if(Ext.getCmp('brc_kind').value=='其他收款' || Ext.getCmp('brc_kind').value=='拆分'){
    					if(Ext.getCmp('ca_asstype') && Ext.isEmpty(Ext.getCmp('ca_asstype').getValue())){
        					btn.setDisabled(true);
        				} else {
        					btn.setDisabled(false);
        				}
    				}else{
    					me.getFeeCate();
    				}
    			}
    		},
    		'erpFormPanel' : {
    			afterload : function(form) {
    				var t = form.down('#brc_kind');
    				this.hidecolumns(t);
				}
    		},
    		'field[name=brc_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=brc_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'textfield[name=brc_cmcurrency]':{
				beforerender: function(field){
					if(Ext.getCmp('brc_ppcode')&&Ext.getCmp('brc_ppcode').value!=""){
						field.readOnly=true;
					}
				}
			},
			'field[name=bap_vendcode]':{
				beforerender: function(field){
					if(Ext.getCmp('brc_ppcode')&& !Ext.isEmpty(Ext.getCmp('brc_ppcode').value)){
						field.readOnly=true;
					}
				}
			},
			'field[name=brc_kind]':{
				beforerender: function(field){
					if(Ext.getCmp('brc_ppcode')&& !Ext.isEmpty(Ext.getCmp('brc_ppcode').value)){
						field.readOnly=true;
					}
					if(Ext.getCmp('brc_sourcetype') && !Ext.isEmpty(Ext.getCmp('brc_sourcetype').value)){
						field.readOnly=true;
					}
				}
			},
			'field[name=brc_billkind2]':{
				beforerender: function(field){
					if(Ext.getCmp('brc_ppcode')&& !Ext.isEmpty(Ext.getCmp('brc_ppcode').value)){
						field.readOnly=true;
					}
				}
			},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn), bool = true;
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				if(Ext.getCmp('brc_kind').value == '背书转让'){
    					var brc_billkind2 = Ext.getCmp('brc_billkind2'), brc_vendcode = Ext.getCmp('brc_vendcode');
    					if(brc_billkind2 && Ext.isEmpty(brc_billkind2.value)){
    						bool = false;
							showError('付款类型未填写');return;
    					}
    					if(brc_vendcode && Ext.isEmpty(brc_vendcode.value)){
    						bool = false;
							showError('被背书人未填写');return;
    					}
    				}
    				if(Ext.getCmp('brc_kind').value == '背书转让(客户)'){
    					var brc_billkind3 = Ext.getCmp('brc_billkind3'), brc_cucode = Ext.getCmp('brc_cucode');
    					if(brc_billkind3 && Ext.isEmpty(brc_billkind3.value)){
    						bool = false;
    						showError('退款类型未填写');return;
    					}
    					if(brc_cucode && Ext.isEmpty(brc_cucode.value)){
    						bool = false;
    						showError('被背书人未填写');return;
    					}
    				}
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items;
					//贷方金额不能大于票面余额
					Ext.each(items, function(item){
						if(!Ext.isEmpty(item.data['brd_barcode'])){
							if(item.data['brd_amount'] > item.data['bar_leftamount']){
								bool = false;
								showError('明细表第' + item.data['brd_detno'] + '行的贷方金额不能大于票面余额');return;
							}
						}
					});
    				this.getAmount();
    				if(bool){
    					if(! me.FormUtil.checkForm()){
    						return;
    					}
    					if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
    						me.FormUtil.getSeqId(form);
    					}
    					var detail = Ext.getCmp('grid');
    					var param2 = new Array();

    					if(Ext.getCmp('assmainbutton')){
    						Ext.each(Ext.Object.getKeys(Ext.getCmp('assmainbutton').cacheStoreForm), function(key){
    							Ext.each(Ext.getCmp('assmainbutton').cacheStoreForm[key], function(d){
    								d['ass_conid'] = key;
    								param2.push(d);
    							});
    						});	
    					}
    					var param1 = me.GridUtil.getGridStore(detail);
    					if(Ext.isEmpty(me.FormUtil.checkFormDirty(form)) && (param1.length == 0) && param2.length == 0){
    						showError($I18N.common.grid.emptyDetail);
    						return;
    					} else {
    						param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
    						param2 = param2 == null ? [] : Ext.encode(param2).replace(/\\/g,"%");
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
    							me.FormUtil.save(r, param1, param2);
    						}else{
    							me.FormUtil.checkForm();
    						}
    					}
    				}
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('brc_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				me.beforeUpdate(me.getForm(btn));
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addBillARChange', '新增应收票据异动作业', 'jsps/fa/gs/billARChange.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('brc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items, bool = true;
    				if(Ext.getCmp('brc_kind').value == '背书转让'){
    					var brc_billkind2 = Ext.getCmp('brc_billkind2'), brc_vendcode = Ext.getCmp('brc_vendcode');
    					if(brc_billkind2 && Ext.isEmpty(brc_billkind2.value)){
    						bool = false;
							showError('付款类型未填写');return;
    					}
    					if(brc_vendcode && Ext.isEmpty(brc_vendcode.value)){
    						bool = false;
							showError('被背书人未填写');return;
    					}
    				}
    				if(Ext.getCmp('brc_kind').value == '背书转让(客户)'){
    					var brc_billkind3 = Ext.getCmp('brc_billkind3'), brc_cucode = Ext.getCmp('brc_cucode');
    					if(brc_billkind3 && Ext.isEmpty(brc_billkind3.value)){
    						bool = false;
    						showError('退款类型未填写');return;
    					}
    					if(brc_cucode && Ext.isEmpty(brc_cucode.value)){
    						bool = false;
    						showError('被背书人未填写');return;
    					}
    				}
    				if(Ext.getCmp('brc_kind').value == '贴现'){
    					if(!Ext.isEmpty(Ext.getCmp('brc_netdiscount').value)){
    						if(Ext.getCmp('brc_amount').value > 0 && Ext.getCmp('brc_netdiscount').value > Ext.getCmp('brc_amount').value){
    							bool = false;
    							showError('贴现净额不能大于贷方总额！');return;
    						}
    					}
    				}
					//贷方金额不能大于票面余额
					Ext.each(items, function(item){
						if(!Ext.isEmpty(item.data['brd_barcode'])){
							if(item.data['brd_amount'] > item.data['bar_leftamount']){
								bool = false;
								showError('明细表第' + item.data['brd_detno'] + '行的贷方金额不能大于票面余额');return;
							}
							if( Ext.getCmp('brc_kind').value == '背书转让' && item.data['brd_catecurrency'] != Ext.getCmp('brc_currency').value ){
								bool = false;
								showError('从表币别与主表币别不一致，行' + item.data['brd_detno']);return;
							}
						}
					});
    				this.getAmount();
    				if(bool)
    					me.FormUtil.onSubmit(Ext.getCmp('brc_id').value, false, this.beforeUpdate, this, me.getForm(btn));
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('brc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('brc_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('brc_statuscode'), type = Ext.getCmp('brc_kind');;
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    				if(type && (type.value == '拆分')){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items, bool = true;
    				if(Ext.getCmp('brc_kind').value == '背书转让'){
    					var brc_billkind2 = Ext.getCmp('brc_billkind2'), brc_vendcode = Ext.getCmp('brc_vendcode');
    					if(brc_billkind2 && Ext.isEmpty(brc_billkind2.value)){
    						bool = false;
							showError('付款类型未填写');return;
    					}
    					if(brc_vendcode && Ext.isEmpty(brc_vendcode.value)){
    						bool = false;
							showError('被背书人未填写');return;
    					}
    				}
    				if(Ext.getCmp('brc_kind').value == '背书转让(客户)'){
    					var brc_billkind3 = Ext.getCmp('brc_billkind3'), brc_cucode = Ext.getCmp('brc_cucode');
    					if(brc_billkind3 && Ext.isEmpty(brc_billkind3.value)){
    						bool = false;
    						showError('退款类型未填写');return;
    					}
    					if(brc_cucode && Ext.isEmpty(brc_cucode.value)){
    						bool = false;
    						showError('被背书人未填写');return;
    					}
    				}
					//贷方金额不能大于票面余额
					Ext.each(items, function(item){
						if(!Ext.isEmpty(item.data['brd_barcode'])){
							if(item.data['brd_amount'] > item.data['bar_leftamount']){
								bool = false;
								showError('明细表第' + item.data['brd_detno'] + '行的贷方金额不能大于票面余额');return;
							}
						}
					});
					if(bool)
    					me.FormUtil.onAudit(Ext.getCmp('brc_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('brc_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('brc_id').value);
    			}
    		},
    		'erpAccountedButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('brc_statuscode'), kind = Ext.getCmp('brc_kind').value;
    				if(status && status.value != 'AUDITED' && status.value != 'COMMITED'){
    					btn.hide();
    				}
    				if(kind == '拆分'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items, bool = true;
    				if(Ext.getCmp('brc_kind').value == '背书转让'){
    					var brc_billkind2 = Ext.getCmp('brc_billkind2'), brc_vendcode = Ext.getCmp('brc_vendcode');
    					if(brc_billkind2 && Ext.isEmpty(brc_billkind2.value)){
    						bool = false;
							showError('付款类型未填写');return;
    					}
    					if(brc_vendcode && Ext.isEmpty(brc_vendcode.value)){
    						bool = false;
							showError('被背书人未填写');return;
    					}
    				}
    				if(Ext.getCmp('brc_kind').value == '背书转让(客户)'){
    					var brc_billkind3 = Ext.getCmp('brc_billkind3'), brc_cucode = Ext.getCmp('brc_cucode');
    					if(brc_billkind3 && Ext.isEmpty(brc_billkind3.value)){
    						bool = false;
    						showError('退款类型未填写');return;
    					}
    					if(brc_cucode && Ext.isEmpty(brc_cucode.value)){
    						bool = false;
    						showError('被背书人未填写');return;
    					}
    				}
    				if(Ext.getCmp('brc_kind').value == '贴现'){
    					if(!Ext.isEmpty(Ext.getCmp('brc_netdiscount').value)){
    						if(Ext.getCmp('brc_amount').value > 0 && Ext.getCmp('brc_netdiscount').value > Ext.getCmp('brc_amount').value){
    							bool = false;
    							showError('贴现净额不能大于贷方总额！');return;
    						}
    					}
    				}
					//贷方金额不能大于票面余额
					Ext.each(items, function(item){
						if(!Ext.isEmpty(item.data['brd_barcode'])){
							if(item.data['brd_amount'] > item.data['bar_leftamount']){
								bool = false;
								showError('明细表第' + item.data['brd_detno'] + '行的贷方金额不能大于票面余额');return;
							}
						}
					});
					if(bool)
						me.FormUtil.onAccounted(Ext.getCmp('brc_id').value);
    			}
    		},
    		'erpResAccountedButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('brc_statuscode');
    				if(status && status.value != 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAccounted(Ext.getCmp('brc_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				me.FormUtil.onPrint(Ext.getCmp('brc_id').value);
    			}
    		},
    		'combo[name=brc_kind]': {
    			afterrender: function(m){
    				Ext.defer(function(){
    					var btn = Ext.getCmp('assmainbutton');
        				if(!Ext.Array.contains(['贴现','其他收款','拆分'],Ext.getCmp('brc_kind').value)){
        					btn.hide();
        				} else if(Ext.getCmp('brc_kind').value=='其他收款' || Ext.getCmp('brc_kind').value=='拆分'){
        					btn.show();
        				} 
    				}, 200);
    			},
    			change: function(m){
    				var btn = Ext.getCmp('assmainbutton');
    				if(!Ext.Array.contains(['贴现','其他收款','拆分'],Ext.getCmp('brc_kind').value)){
    					btn.hide();
    				} else if(Ext.getCmp('brc_kind').value=='其他收款' || Ext.getCmp('brc_kind').value=='拆分'){
    					btn.show();
    				} else{
    					me.getFeeCate();
    				}
					this.hidecolumns(m);
					if(m.value == '拆分'){
        				showError('拆分类型的异动单不能手工新增!');
        			}
				}
    		},
    		'numberfield[name=brc_amount]':{
    			beforerender:function(num){
    				num.minValue = Number.NEGATIVE_INFINITY;
    				num.setMinValue(num.minValue);
    				b = num.baseChars+"";
    				b += num.decimalSeparator;
    				b += "-";
    				b = Ext.String.escapeRegex(b);
    				num.maskRe = new RegExp("[" + b + "]");
    				if(Ext.getCmp('brc_ppcode')&&Ext.getCmp('brc_ppcode').value!=""){
    					num.readOnly=true;
					}
    			},
    			change: function(f) {
    				if(Ext.getCmp('brc_kind').value == '贴现'){
	    				var v1 = (Ext.getCmp('brc_netdiscount').value || 0),
	    					v2 = (f.value || 0);
	    				if(v1 == 0) {
	    					Ext.getCmp('brc_discountamount').setValue(0);
	    				} else {
	    					Ext.getCmp('brc_discountamount').setValue(Ext.Number.toFixed(v2-v1, 2));
	    				}
    				} else {
    					var v1 = (f.value || 0),
    					v2 = (Ext.getCmp('brc_cmamount').value || 0);
	    				if(v1 == 0) {
	    					Ext.getCmp('brc_cmrate').setValue(0);
	    				} else {
	    					Ext.getCmp('brc_cmrate').setValue(Ext.Number.toFixed(v2/v1, 15));
	    				}
    				}
    			}
    		},
    		'numberfield[name=brc_cmamount]':{
    			change: function(f) {
    				var v1 = (Ext.getCmp('brc_amount').value || 0),
    					v2 = (f.value || 0);
    				if(v1 == 0) {
    					Ext.getCmp('brc_cmrate').setValue(0);
    				} else {
    					Ext.getCmp('brc_cmrate').setValue(Ext.Number.toFixed(v2/v1, 8));
    				}
    			}
    		},
    		'numberfield[name=brc_netdiscount]':{
    			change: function(f) {
    				if(Ext.getCmp('brc_kind').value == '贴现'){
    					var v1 = (Ext.getCmp('brc_amount').value || 0),
	    					v2 = (f.value || 0);
	    				if(v1 == 0) {
	    					Ext.getCmp('brc_discountamount').setValue(0);
	    				} else {
	    					Ext.getCmp('brc_discountamount').setValue(Ext.Number.toFixed(v1-v2, 2));
	    				}
    				}
    			}
    		},
    		'dbfindtrigger[name=brd_barcode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				if(Ext.getCmp('brc_custcode')){
    					var code = Ext.getCmp('brc_custcode').value;
    					if(code != null && code != ''){
    						var obj = me.getCodeCondition();
        					if(obj && obj.field){
        						t.dbBaseCondition = obj.field + "='" + code + "'";
        					}
    					}
    				}
    			},
    			aftertrigger: function(t){
    				if(Ext.getCmp('brc_custcode')){
    					var obj = me.getCodeCondition();
    					if(obj && obj.fields){
    						me.FormUtil.getFieldsValue(obj.tablename, obj.fields, obj.myfield + "='" + t.value + "'", obj.tFields);
    					}
    				}
    				if(Ext.getCmp('brc_catecode')){
    					var obj = me.getCateCondition();
    					if(obj && obj.fields){
    						me.FormUtil.getFieldsValue(obj.tablename, obj.fields, obj.myfield + "='" + t.value + "'", obj.tFields);
    					}
    				}
    			}
    		}
    	});
    }, 
    hidecolumns:function(m){
		if(!Ext.isEmpty(m.getValue())) {
			var form = m.ownerCt;
			form.down('#brc_currency') && form.down('#brc_currency').show();
			form.down('#brc_rate') && form.down('#brc_rate').show();
			if(m.value == '背书转让'){
				form.down('#brc_vendcode') && form.down('#brc_vendcode').show();
				form.down('#brc_vendname') && form.down('#brc_vendname').show();
				form.down('#brc_billkind2') && form.down('#brc_billkind2').show();
				form.down('#brc_billkind1') && form.down('#brc_billkind1').hide();
				form.down('#brc_custcode') && form.down('#brc_custcode').show();
				form.down('#brc_custname') && form.down('#brc_custname').show();
				form.down('#brc_feecatecode') && form.down('#brc_feecatecode').hide();
				form.down('#brc_feecatename') && form.down('#brc_feecatename').hide();
				form.down('#brc_netdiscount') && form.down('#brc_netdiscount').hide();
				form.down('#brc_discountrate') && form.down('#brc_discountrate').hide();
				form.down('#brc_discountamount') && form.down('#brc_discountamount').hide();
				form.down('#brc_cmcurrency') && form.down('#brc_cmcurrency').show();
				form.down('#brc_cmrate') && form.down('#brc_cmrate').show();
				form.down('#brc_cmamount') && form.down('#brc_cmamount').show();
				form.down('#brc_ppcode') && form.down('#brc_ppcode').show();
				if(Ext.isEmpty(Ext.getCmp('brc_cmcurrency').value)){
					Ext.getCmp('brc_cmcurrency').setValue('RMB');
					Ext.getCmp('brc_cmrate').setValue('1');
				}
				if(Ext.isEmpty(Ext.getCmp('brc_currency').value)){
					Ext.getCmp('brc_currency').setValue('RMB');
					Ext.getCmp('brc_rate').setValue('1');
				}
				form.down('#brc_catecode') && form.down('#brc_catecode').hide();
				form.down('#brc_catename') && form.down('#brc_catename').hide();
				form.down('#brc_billkind3') && form.down('#brc_billkind3').hide();
				form.down('#brc_cucode') && form.down('#brc_cucode').hide();
				form.down('#brc_cuname') && form.down('#brc_cuname').hide();
			} else if(m.value == '背书转让(客户)'){
				form.down('#brc_vendcode') && form.down('#brc_vendcode').hide();
				form.down('#brc_vendname') && form.down('#brc_vendname').hide();
				form.down('#brc_billkind2') && form.down('#brc_billkind2').hide();
				form.down('#brc_billkind1') && form.down('#brc_billkind1').hide();
				form.down('#brc_custcode') && form.down('#brc_custcode').show();
				form.down('#brc_custname') && form.down('#brc_custname').show();
				form.down('#brc_feecatecode') && form.down('#brc_feecatecode').hide();
				form.down('#brc_feecatename') && form.down('#brc_feecatename').hide();
				form.down('#brc_netdiscount') && form.down('#brc_netdiscount').hide();
				form.down('#brc_discountrate') && form.down('#brc_discountrate').hide();
				form.down('#brc_discountamount') && form.down('#brc_discountamount').hide();
				form.down('#brc_cmcurrency') && form.down('#brc_cmcurrency').show();
				form.down('#brc_cmrate') && form.down('#brc_cmrate').show();
				form.down('#brc_cmamount') && form.down('#brc_cmamount').show();
				form.down('#brc_ppcode') && form.down('#brc_ppcode').hide();
				if(Ext.isEmpty(Ext.getCmp('brc_cmcurrency').value)){
					Ext.getCmp('brc_cmcurrency').setValue('RMB');
					Ext.getCmp('brc_cmrate').setValue('1');
				}
				if(Ext.isEmpty(Ext.getCmp('brc_currency').value)){
					Ext.getCmp('brc_currency').setValue('RMB');
					Ext.getCmp('brc_rate').setValue('1');
				}
				form.down('#brc_catecode') && form.down('#brc_catecode').hide();
				form.down('#brc_catename') && form.down('#brc_catename').hide();
				form.down('#brc_billkind3') && form.down('#brc_billkind3').show();
				form.down('#brc_cucode') && form.down('#brc_cucode').show();
				form.down('#brc_cuname') && form.down('#brc_cuname').show();
			} else if(m.value == '背书借'){
				form.down('#brc_vendcode').hide();
				form.down('#brc_vendname').hide();
				form.down('#brc_billkind2').hide();
				form.down('#brc_billkind1').show();
				form.down('#brc_custcode').show();
				form.down('#brc_custname').show();
				form.down('#brc_feecatecode') && form.down('#brc_feecatecode').hide();
				form.down('#brc_feecatename') && form.down('#brc_feecatename').hide();
				form.down('#brc_netdiscount') && form.down('#brc_netdiscount').hide();
				form.down('#brc_discountrate') && form.down('#brc_discountrate').hide();
				form.down('#brc_discountamount') && form.down('#brc_discountamount').hide();
				form.down('#brc_cmcurrency') && form.down('#brc_cmcurrency').hide();
				form.down('#brc_cmrate') && form.down('#brc_cmrate').hide();
				form.down('#brc_cmamount') && form.down('#brc_cmamount').hide();
				form.down('#brc_ppcode') && form.down('#brc_ppcode').hide();
				form.down('#brc_billkind3').hide();
				form.down('#brc_cucode') && form.down('#brc_cucode').hide();
				form.down('#brc_cuname') && form.down('#brc_cuname').hide();
			}else if(m.value == '贴现'){
				form.down('#brc_vendcode').hide();
				form.down('#brc_vendname').hide();
				form.down('#brc_custcode').show();
				form.down('#brc_custname').show();
				form.down('#brc_billkind2').hide();
				form.down('#brc_billkind1').hide();
				form.down('#brc_feecatecode') && form.down('#brc_feecatecode').show();
				form.down('#brc_feecatename') && form.down('#brc_feecatename').show();
				form.down('#brc_netdiscount') && form.down('#brc_netdiscount').show();
				form.down('#brc_discountrate') && form.down('#brc_discountrate').show();
				form.down('#brc_discountamount') && form.down('#brc_discountamount').show();
				form.down('#brc_currency') && form.down('#brc_currency').hide();
				form.down('#brc_rate') && form.down('#brc_rate').hide();
				form.down('#brc_cmcurrency') && form.down('#brc_cmcurrency').hide();
				form.down('#brc_cmrate') && form.down('#brc_cmrate').hide();
				form.down('#brc_cmamount') && form.down('#brc_cmamount').hide();
				form.down('#brc_ppcode') && form.down('#brc_ppcode').hide();
				form.down('#brc_billkind3') && form.down('#brc_billkind3').hide();
				form.down('#brc_cucode') && form.down('#brc_cucode').hide();
				form.down('#brc_cuname') && form.down('#brc_cuname').hide();
			}else if (m.value == '收款'){ 
				form.down('#brc_vendcode').hide();
				form.down('#brc_vendname').hide();
				form.down('#brc_custcode').show();
				form.down('#brc_custname').show();
				form.down('#brc_billkind2').hide();
				form.down('#brc_billkind1').hide();
				form.down('#brc_feecatecode') && form.down('#brc_feecatecode').hide();
				form.down('#brc_feecatename') && form.down('#brc_feecatename').hide();
				form.down('#brc_netdiscount') && form.down('#brc_netdiscount').hide();
				form.down('#brc_discountrate') && form.down('#brc_discountrate').hide();
				form.down('#brc_discountamount') && form.down('#brc_discountamount').hide();
				form.down('#brc_currency') && form.down('#brc_currency').hide();
				form.down('#brc_rate') && form.down('#brc_rate').hide();
				form.down('#brc_cmcurrency') && form.down('#brc_cmcurrency').hide();
				form.down('#brc_cmrate') && form.down('#brc_cmrate').hide();
				form.down('#brc_cmamount') && form.down('#brc_cmamount').hide();
				form.down('#brc_ppcode') && form.down('#brc_ppcode').hide();
				form.down('#brc_billkind3') && form.down('#brc_billkind3').hide();
				form.down('#brc_cucode') && form.down('#brc_cucode').hide();
				form.down('#brc_cuname') && form.down('#brc_cuname').hide();
			} else if (m.value == '退票'){
				form.down('#brc_vendcode').hide();
				form.down('#brc_vendname').hide();
				form.down('#brc_custcode').show();
				form.down('#brc_custname').show();
				form.down('#brc_billkind2').hide();
				form.down('#brc_billkind1').hide();
				form.down('#brc_feecatecode') && form.down('#brc_feecatecode').hide();
				form.down('#brc_feecatename') && form.down('#brc_feecatename').hide();
				form.down('#brc_netdiscount') && form.down('#brc_netdiscount').hide();
				form.down('#brc_discountrate') && form.down('#brc_discountrate').hide();
				form.down('#brc_discountamount') && form.down('#brc_discountamount').hide();
				form.down('#brc_cmcurrency') && form.down('#brc_cmcurrency').hide();
				form.down('#brc_cmrate') && form.down('#brc_cmrate').hide();
				form.down('#brc_cmamount') && form.down('#brc_cmamount').hide();
				form.down('#brc_ppcode') && form.down('#brc_ppcode').hide();
				form.down('#brc_catecode') && form.down('#brc_catecode').hide();
				form.down('#brc_catename') && form.down('#brc_catename').hide();
				form.down('#brc_billkind3') && form.down('#brc_billkind3').hide();
				form.down('#brc_cucode') && form.down('#brc_cucode').hide();
				form.down('#brc_cuname') && form.down('#brc_cuname').hide();
			} else if(m.value == '拆分'){
				form.down('#brc_vendcode') && form.down('#brc_vendcode').hide();
				form.down('#brc_vendname') && form.down('#brc_vendname').hide();
				form.down('#brc_billkind2') && form.down('#brc_billkind2').hide();
				form.down('#brc_billkind1') && form.down('#brc_billkind1').hide();
				form.down('#brc_custcode') && form.down('#brc_custcode').show();
				form.down('#brc_custname') && form.down('#brc_custname').show();
				form.down('#brc_feecatecode') && form.down('#brc_feecatecode').hide();
				form.down('#brc_feecatename') && form.down('#brc_feecatename').hide();
				form.down('#brc_netdiscount') && form.down('#brc_netdiscount').hide();
				form.down('#brc_discountrate') && form.down('#brc_discountrate').hide();
				form.down('#brc_discountamount') && form.down('#brc_discountamount').hide();
				form.down('#brc_cmcurrency') && form.down('#brc_cmcurrency').hide();
				form.down('#brc_cmrate') && form.down('#brc_cmrate').hide();
				form.down('#brc_cmamount') && form.down('#brc_cmamount').hide();
				form.down('#brc_ppcode') && form.down('#brc_ppcode').hide();
				if(Ext.isEmpty(Ext.getCmp('brc_cmcurrency').value)){
					Ext.getCmp('brc_cmcurrency').setValue('RMB');
					Ext.getCmp('brc_cmrate').setValue('1');
				}
				if(Ext.isEmpty(Ext.getCmp('brc_currency').value)){
					Ext.getCmp('brc_currency').setValue('RMB');
					Ext.getCmp('brc_rate').setValue('1');
				}
				form.down('#brc_catecode') && form.down('#brc_catecode').show();
				form.down('#brc_catename') && form.down('#brc_catename').show();
				form.down('#brc_billkind3') && form.down('#brc_billkind3').hide();
				form.down('#brc_cucode') && form.down('#brc_cucode').show();
				form.down('#brc_cuname') && form.down('#brc_cuname').show();
			} else{ 
				form.down('#brc_vendcode').hide();
				form.down('#brc_vendname').hide();
				form.down('#brc_custcode').hide();
				form.down('#brc_custname').hide();
				form.down('#brc_billkind2').hide();
				form.down('#brc_billkind1').hide();
				form.down('#brc_feecatecode') && form.down('#brc_feecatecode').hide();
				form.down('#brc_feecatename') && form.down('#brc_feecatename').hide();
				form.down('#brc_netdiscount') && form.down('#brc_netdiscount').hide();
				form.down('#brc_discountrate') && form.down('#brc_discountrate').hide();
				form.down('#brc_discountamount') && form.down('#brc_discountamount').hide();
				form.down('#brc_cmcurrency') && form.down('#brc_cmcurrency').hide();
				form.down('#brc_cmrate') && form.down('#brc_cmrate').hide();
				form.down('#brc_cmamount') && form.down('#brc_cmamount').hide();
				form.down('#brc_ppcode') && form.down('#brc_ppcode').hide();
				form.down('#brc_billkind3') && form.down('#brc_billkind3').hide();
				form.down('#brc_cucode') && form.down('#brc_cucode').hide();
				form.down('#brc_cuname') && form.down('#brc_cuname').hide();
			}
		}
	},
	 //计算借方金额   并写入主表借方总额字段
	getAmount: function(){
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var amount = 0;
		var netdiscount = 0;
		var kind = Ext.getCmp('brc_kind').value;
		var ppcode = Ext.getCmp('brc_ppcode').value;
		Ext.each(items,function(item,index){
			if(item.data['brd_barcode']!=null&&item.data['brd_barcode']!=""){
				amount= amount + Number(item.data['brd_amount']);
			}
		});
		if(Ext.isEmpty(ppcode)){
			Ext.getCmp('brc_amount').setValue(Ext.Number.toFixed(amount, 2));
		}
		if(kind == '贴现'){
			if(typeof (f = Ext.getCmp('brc_netdiscount')) != 'undefined' && typeof (f = Ext.getCmp('brc_discountamount')) != 'undefined'){
				netdiscount = Ext.getCmp('brc_netdiscount').value;
				Ext.getCmp('brc_discountamount').setValue(Ext.Number.toFixed(Ext.getCmp('brc_amount').value-netdiscount, 2));
			}
		}
	},
    beforeSubmit:function(btn){
    	var me = this;
    	var grid = Ext.getCmp('grid'),items=grid.store.data.items;
    	var amount = Number(Ext.getCmp('brc_amount').getValue());
    	var detailamount = 0;
    	Ext.each(items,function(item,index){
    		detailamount = detailamount+Number(item.data['brd_amount']);
    		if(Ext.Number.toFixed(amount, 2) != Ext.Number.toFixed(detailamount, 2)){
    			//抛出异常
    			showError('明细行贷方金额与贷方总额不等,不能提交');return;
    		}
			me.FormUtil.onSubmit(Ext.getCmp('brc_id').value);
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getCodeCondition: function(){
		var field = "bar_custcode";
		var tFields = 'brc_custcode,brc_custname';
		var fields = 'bar_custcode,bar_custname';
		var tablename = 'BillAR';
		var myfield = 'bar_code';
		var obj = new Object();
		obj.field = field;
		obj.fields = fields;
		obj.tFields = tFields;
		obj.tablename = tablename;
		obj.myfield = myfield;
		return obj;
	},
	getCateCondition: function(){
		var field = "bar_bankcode";
		var tFields = 'brc_catecode,brc_catename';
		var fields = 'bar_bankcode,bar_bank';
		var tablename = 'BillAR';
		var myfield = 'bar_code';
		var obj = new Object();
		obj.field = field;
		obj.fields = fields;
		obj.tFields = tFields;
		obj.tablename = tablename;
		obj.myfield = myfield;
		return obj;
	},
	beforeUpdate: function(form){
		var me = this;
		var grid = Ext.getCmp('grid'), items = grid.store.data.items, bool = true;
		if(Ext.getCmp('brc_kind').value == '背书转让'){
			var brc_billkind2 = Ext.getCmp('brc_billkind2'), brc_vendcode = Ext.getCmp('brc_vendcode');
			if(brc_billkind2 && Ext.isEmpty(brc_billkind2.value)){
				bool = false;
				showError('付款类型未填写');return;
			}
			if(brc_vendcode && Ext.isEmpty(brc_vendcode.value)){
				bool = false;
				showError('被背书人未填写');return;
			}
		}
		if(Ext.getCmp('brc_kind').value == '背书转让(客户)'){
			var brc_billkind3 = Ext.getCmp('brc_billkind3'), brc_cucode = Ext.getCmp('brc_cucode');
			if(brc_billkind3 && Ext.isEmpty(brc_billkind3.value)){
				bool = false;
				showError('退款类型未填写');return;
			}
			if(brc_cucode && Ext.isEmpty(brc_cucode.value)){
				bool = false;
				showError('被背书人未填写');return;
			}
		}
		//贷方金额不能大于票面余额
		Ext.each(items, function(item){
			if(!Ext.isEmpty(item.data['brd_barcode'])){
				if(item.data['brd_amount'] > item.data['bar_leftamount']){
					bool = false;
					showError('明细表第' + item.data['brd_detno'] + '行的贷方金额不能大于票面余额');return;
				}
			}
		});
		this.getAmount();
		if(bool){
			if(! me.FormUtil.checkForm()){
				return;
			}
			if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
				me.FormUtil.getSeqId(form);
			}
			var detail = Ext.getCmp('grid');
			var param2 = new Array();
			if(Ext.getCmp('assmainbutton')){
				Ext.each(Ext.Object.getKeys(Ext.getCmp('assmainbutton').cacheStoreForm), function(key){
					Ext.each(Ext.getCmp('assmainbutton').cacheStoreForm[key], function(d){
						d['ass_conid'] = key;
						param2.push(d);
					});
				});	
			}
			var param1 = me.GridUtil.getGridStore(detail);
			if(Ext.isEmpty(me.FormUtil.checkFormDirty(form)) && (param1.length == 0)
					&& param2.length == 0){
				showError($I18N.common.grid.emptyDetail);
				return;
			} else {
				param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
				param2 = param2 == null ? [] : Ext.encode(param2).replace(/\\/g,"%");
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
					me.FormUtil.update(r, param1, param2);
				}else{
					me.FormUtil.checkForm();
				}
			}
		}
	},
	getFeeCate: function(){
		var me = this;
		var feecatecode = Ext.getCmp('brc_feecatecode');
		if(feecatecode&&feecatecode.value){
			condition = "ca_code = '"+feecatecode.value+"'";
			Ext.Ajax.request({
                    url: basePath + 'common/getFieldsData.action',
                    params: {
                        caller: 'Category',
                        fields: 'ca_asstype,ca_assname',
                        condition: condition
                    },
                    method: 'post',
                    callback: function(options, success, response) {
                        var res = new Ext.decode(response.responseText);
                        if (res.exception || res.exceptionInfo) {
                            showError(res.exceptionInfo);
                            return;
                        }else{
                        	var asstype = res.data.ca_asstype;
                        	var caasstype = Ext.getCmp('ca_asstype');
                        	var caassname = Ext.getCmp('ca_assname');
                        	var btn = Ext.getCmp('assmainbutton');
                        	if(asstype){
                        		caasstype && caasstype.setValue(asstype);
                        		caassname && caassname.setValue(res.data.ca_assname);
                        		btn && btn.show();
                        	}else{
                        		btn && btn.hide();
                        	}
                        }
                    }
                });
		}
	}
});