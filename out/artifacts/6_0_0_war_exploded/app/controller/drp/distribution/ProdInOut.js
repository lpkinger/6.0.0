Ext.QuickTips.init();
Ext.define('erp.controller.drp.distribution.ProdInOut', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
      		'core.form.Panel','drp.distribution.ProdInOut','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.button.Banned','core.button.ResBanned','core.button.Post','core.button.ResPost','core.button.Query','core.button.GetPrice',
  			'core.button.RePrice','core.button.BussAccount','core.button.Export','core.form.FtFindField','core.form.ConDateField',
  			'core.button.FeeShare', 'core.button.TurnDefectOut',
  			'core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.trigger.TextAreaTrigger'
      	],
    init:function(){
    	var me = this;
    	me.FormUtil = Ext.create('erp.util.FormUtil');
        me.GridUtil = Ext.create('erp.util.GridUtil');
        me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		'erpGridPanel2': { 
    			afterrender: function(grid){
    				grid.plugins[0].on('beforeedit', function(args){
    					if(args.field == "pd_inqty") {
    						return me.isAllowUpdateQty(args.record);
    					}
    				});
    			},
    			itemclick: function(selModel, record){
    				var bool = me.hasSource(selModel.ownerCt);
    				if(!bool)
    					this.GridUtil.onGridItemClick(selModel, record);
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber(caller, 2, form.codeField);//自动添加编号
    				}
    				me.save(btn);
    			}
    		},
    		'erpDeleteButton' : {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value == 'DELETED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onDelete({pu_id: Number(Ext.getCmp('pi_id').value)});
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    				status = Ext.getCmp('pi_statuscode');
    				if(status && 'POSTED' == status.value) {
    					btn.hide();
    				}
    			},
    			click: function(btn){
    			    var grid = Ext.getCmp('grid'), items = grid.store.data.items, c = Ext.getCmp('pi_inoutno').value;
    				var piclass = Ext.getCmp('pi_class').value, whcode = Ext.getCmp('pi_whcode');
    			    Ext.Array.each(items, function(item){
    			    	if(!Ext.isEmpty(item.data['pd_prodcode'])){
    			    		item.set('pd_inoutno', c);
    				    	item.set('pd_piclass', piclass);
    				    	if(whcode && item.data['pd_whcode'] == null && item.data['pd_whcode'] == ''){
    				    		item.set('pd_whcode', whcode.value);
    				    	}
    			    	}
    				});
    				me.FormUtil.onUpdate(me);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('add' + caller, '新增出入库单', "jsps/drp/distribution/prodInOut.jsp?whoami=" + caller);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var bool = true;
    				Ext.each(items, function(item){
    					if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
    						if(Ext.getCmp('pd_inqty') && item.data['pd_inqty'] == null || item.data['pd_inqty'] == ''){
	    						bool = false;
	    						showError("明细第" + item.data['pd_pdno'] + "行未填写数量，不能提交");return;
    						}
    					}
    				});
    				if(bool){
    					me.FormUtil.onSubmit(Ext.getCmp('pi_id').value);
    				}  				
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('pi_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('pi_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('pi_id').value);
    			}
    		},
    		'erpBannedButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onBanned(Ext.getCmp('pi_id').value);
    			}
    		},
    		'erpResBannedButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'BANNED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResBanned(Ext.getCmp('pi_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click:function(btn){
    			var whichKind=Ext.getCmp('pi_class').value;
    			console.log(whichKind);
				var reportName = '';
				if(whichKind=="配货单"){
					reportName="sendlist_nosale";
				}else if(whichKind=="不良品入库单"){
					console.log("fds");
					reportName="pio_notokin";
				}else if(whichKind=="不良品出库单"){
					reportName="pio_notokout";
				}else if(whichKind=="其它采购入库单"){
					reportName="piolist_opin";
				}else if(whichKind=="其它采购出库单"){
					reportName="piolist_opout";
				}else if(whichKind=="分公司拨入单"){
					reportName="piolist_bc";
				}else if(whichKind=="分公司拨出单"){
					reportName="piolist_bc";
				}else if(whichKind=="其它入库单"){
					reportName="piolist_in";
				}else if(whichKind=="其它出库单"){
					reportName="piolist_out";
				}else if(whichKind=="报废单"){
					reportName="piolist_bf";
				}else if(whichKind=="退换货入库单"){
					reportName="pio_changein";
				}else if(whichKind=="退换货出库单"){
					reportName="pio_changeout";
				}else if(whichKind=="销售退货单"){
					reportName="retulist";
				}else if(whichKind=="采购验收单"){
					reportName="acclist";
				}else if(whichKind=="采购验退单"){
					reportName="piolist_yt";
				}else if(whichKind=="销售拨入单"){
					reportName="piolist";
				}else if(whichKind=="销售拨出单"){
					reportName="piolist_salebc";
				}else if(whichKind=="生产领料单"){
					reportName="PIOLISTM";
				}else if(whichKind=="生产退料单"){
					reportName="PIOLISTM_Back";
				}else if(whichKind=="完工入库单"){
					reportName="finish";
				}else if(whichKind=="结余退料单"){
					reportName="PIOLISTM_JY";
				}else if(whichKind=="拆件入库单"){
					reportName="chaijian";
				}else if(whichKind=="生产补料单"){
					reportName="PIOLIST_bl";
				}else if(whichKind=="生产耗料单"){
					reportName="PIOLISTM_HL";
				}else if(whichKind=="委外领料单"){
					reportName="Expiolist";
				}else if(whichKind=="委外退料单"){
					reportName="PIOLIST_wwtl";
				}else if(whichKind=="委外验收单"){
					reportName="EXPLIST_ys";
				}else if(whichKind=="委外验退单"){
					reportName="EXPLIST_yt";
				}else if(whichKind="生产报废单"){
					reportName=="MakeScrap";
				}
							
				var condition = '{prodinout.pi_id}=' + Ext.getCmp('pi_id').value + '';
				var id = Ext.getCmp('pi_id').value;
				me.FormUtil.onwindowsPrint(id, reportName, condition);
			}
    		},
    		'erpPostButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pi_statuscode');
    				if(status && status.value != 'UNPOST'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onPost(Ext.getCmp('pi_id').value);
    			}
    		},
    		'erpResPostButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pi_statuscode');
    				if(status && status.value != 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResPost(Ext.getCmp('pi_id').value);
    			}
    		},
    		'field[name=pi_cardcode]': {
    			afterrender: function(f){
    				if(f.value != null && f.value != ''){
    					f.setReadOnly(true);
    					f.setFieldStyle(f.fieldStyle + ';background:#f1f1f1;');
    				}
    			}
    		},
    		'dbfindtrigger[name=pd_ordercode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				if(Ext.getCmp('pi_cardcode')){
    					var code = Ext.getCmp('pi_cardcode').value;
    					if(code != null && code != ''){
    						var obj = me.getCodeCondition();
        					if(obj && obj.field){
        						t.dbBaseCondition = obj.field + "='" + code + "'";
        					}
        				}
    				}
    			},
    			aftertrigger: function(t){
    				if(Ext.getCmp('pi_cardcode')){
    					var obj = me.getCodeCondition();
    					me.FormUtil.getFieldsValue(obj.tablename, obj.fields, obj.myfield + "='" + t.value + "'", obj.tFields);
    				}
    			}
    		},
    		'dbfindtrigger[name=pd_orderdetno]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				var code = record.data['pd_ordercode'];
    				if(code == null || code == ''){
    					showError("请先选择关联单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					var field = me.getBaseCondition();
    					if(field){
    						t.dbBaseCondition = field + "='" + code + "'";
    					}
    				}
    			}
    		},
    		'multidbfindtrigger[name=pd_orderdetno]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				var code = record.data['pd_ordercode'];
    				if(code == null || code == ''){
    					showError("请先选择关联单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					var field = me.getBaseCondition();
    					if(field){
    						t.dbBaseCondition = field + "='" + code + "'";
    					}
    				}
    			}
    		},
    		'dbfindtrigger[name=pd_batchcode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				var pr = record.data['pd_prodcode'];
    				if(pr == null || pr == ''){
    					showError("请先选择料号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					var code = record.data['pd_whcode'];
        				if(code == null || code == ''){
        					if(Ext.getCmp('pi_whcode')) {
        						code = Ext.getCmp('pi_whcode').value;
            					if(code == null || code == ''){
            						showError("请先选择仓库!");
                					t.setHideTrigger(true);
                					t.setReadOnly(true);
            					} else {
            						t.dbBaseCondition = "ba_whcode='" + code + "' AND ba_prodcode='" + pr + "'";
            					}
        					}
        				} else {
        					t.dbBaseCondition = "ba_whcode='" + code + "' AND ba_prodcode='" + pr + "'";
        				}
    				}
    			}
    		},
    		'field[name=pi_whcode]': {
    			change: function(f){
    				if(f.value != null && f.value != ''){
    					var grid = Ext.getCmp('grid');
    				    Ext.Array.each(grid.store.data.items, function(item){
    				    	if(item.data['pd_whcode'] == null || item.data['pd_whcode'] == ''){
    				    		item.set('pd_whcode', f.value);
    				    	}
    					});
    				}
    			}
    		},
    		'erpGetPriceButton': {
    			click: function(){
    				
    			}
    		}
    	});
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	save: function(btn){
		var me = this;
		var form = me.getForm(btn);
		if(Ext.getCmp('Fin_Code')){
			Ext.getCmp('Fin_Code').setValue(Ext.getCmp(form.codeField).value);//流水号
		}
		var grid = Ext.getCmp('grid'), items = grid.store.data.items, c = Ext.getCmp('pi_inoutno').value;
		var piclass = Ext.getCmp('pi_class').value, whcode = Ext.getCmp('pi_whcode');
	    Ext.Array.each(items, function(item){
	    	if(!Ext.isEmpty(item.data['pd_prodcode'])){
	    		item.set('pd_inoutno', c);
		    	item.set('pd_piclass', piclass);
		    	if(whcode && item.data['pd_whcode'] == null && item.data['pd_whcode'] == ''){
		    		item.set('pd_whcode', whcode.value);
		    	}
	    	}
		});
		me.FormUtil.beforeSave(me);
	},
	/**
	 * pd_orderdetno的限制条件
	 */
	getBaseCondition: function(){

		var field = null;
		switch (caller) {
			case 'ProdInOut!PurcCheckin': //采购验收单
				field = "pd_code";break;
			case 'ProdInOut!PurcCheckout': //采购验退单
				field = "pd_code";break;
			case 'ProdInOut!DrpSale': //配货单
				field = "sd_code";break;
			case 'ProdInOut!Make!Return': //生产退料单
				field = "mm_code";break;
			case 'ProdInOut!Make!Give': //生产补料单
				field = "mm_code";break;
			case 'ProdInOut!Picking': //生产领料单
				field = "mm_code";break;
			case 'ProdInOut!Make!Consume': //生产耗料单
				field = "mm_code";break;
			case 'ProdInOut!Make!Useless': //生产报废单
				field = "mm_code";break;
			case 'ProdInOut!SaleAppropriationOut': //销售拨出单
				field = "sd_code";break;
			case 'ProdInOut!SaleReturn': //销售退货单
				field = "sd_code";break;	
			case 'ProdInOut!OtherOut': //其它出库单
				field = "sd_code";break;
			case 'ProdInOut!OutsidePicking': //委外领料单
				field = "ma_code";break;
			case 'ProdInOut!OutsideReturn': //委外退料单
				field = "mm_code";break;
			case 'ProdInOut!DefectIn': //不良品入库单
				field = "pd_code";break;
			case 'ProdInOut!DefectOut': //不良品入库单
				field = "pd_code";break;
			case 'ProdInOut!OutsideCheckIn': //委外验收单
				field = "mm_code";break;
			case 'ProdInOut!OSMake!Give': //委外补料单
				field = "mm_code";break;
		}
		return field;
	},
	/**
	 * pd_ordercode的限制条件
	 */
	getCodeCondition: function(){
		var field = null;
		var fields = '';
		var tablename = '';
		var myfield = '';
		var tFields = '';
		switch (caller) {
			case 'ProdInOut!PurcCheckin': //采购验收单
				field = "pu_vendcode";
				tFields = 'pi_cardid,pi_cardcode,pi_title,pi_currency,pi_rate,pi_payment,pi_transport,pi_paydate';
				fields = 'pu_vendid,pu_vendcode,pu_vendname,pu_currency,pu_rate,pu_payments,pu_transport,pu_suredate';
				tablename = 'Purchase';
				myfield = 'pu_code';
				break;
			case 'ProdInOut!PurcCheckout': //采购验退单
				field = "pu_vendcode";
				tFields = 'pi_cardid,pi_cardcode,pi_title,pi_currency,pi_rate,pi_payment,pi_transport,pi_paydate';
				fields = 'pu_vendid,pu_vendcode,pu_vendname,pu_currency,pu_rate,pu_payments,pu_transport,pu_suredate';
				tablename = 'Purchase';
				myfield = 'pu_code';
				break;
			case 'ProdInOut!DrpSale': //配货单
				tFields = 'pi_cardid,pi_cardcode,pi_title,pi_currency,pi_rate,pi_payment,pi_transport,pi_sellercode,pi_belongs,pi_receivecode,pi_receivename,pi_transport';
				fields = 'sa_custid,sa_custcode,sa_custname,sa_currency,sa_rate,sa_payments,sa_transport,sa_sellercode,sa_seller,sa_shcustcode,sa_shcustname,sa_transport';
				tablename = 'Sale';
				myfield = 'sa_code';
				field = "sa_custcode";
				break;
			case 'ProdInOut!AppropriationPutIn': //分公司拨入单
				tFields = 'pi_cardcode,pi_title';
				fields = 'ma_custcode,ma_custname';
				tablename = 'Make';
				myfield = 'ma_code';
				field = "ma_custcode";
				break;			
			case 'ProdInOut!SaleAppropriationOut': //销售拨出单
				tFields = 'pi_cardid,pi_cardcode,pi_title,pi_currency,pi_rate,pi_payment,pi_transport';
				fields = 'sa_custid,sa_custcode,sa_custname,sa_currency,sa_rate,sa_payments,sa_transport';
				tablename = 'Sale';
				myfield = 'sa_code';
				field = "sa_custcode";
				break;
			case 'ProdInOut!SaleReturn': //销售退货单
				tFields = 'pi_cardid,pi_cardcode,pi_title,pi_currency,pi_rate,pi_payment,pi_transport,pi_sellercode,pi_belongs,pi_receivecode,pi_receivename,pi_transport';
				fields = 'sa_custid,sa_custcode,sa_custname,sa_currency,sa_rate,sa_payments,sa_transport,sa_sellercode,sa_seller,sa_shcustcode,sa_shcustname,sa_transport';
				tablename = 'Sale';
				myfield = 'sa_code';
				field = "sa_custcode";
				break;
			case 'ProdInOut!AppropriationPutOut': //分公司拨出单
				tFields = 'pi_cardcode,pi_title';
				fields = 'ma_custcode,ma_custname';
				tablename = 'Make';
				myfield = 'ma_code';
				field = "ma_custcode";
				break;	
			case 'ProdInOut!DefectIn': //不良品入库单
				field = "pu_vendcode";
				tFields = 'pi_cardid,pi_cardcode,pi_title,pi_currency,pi_rate,pi_payment,pi_transport,pi_paydate';
				fields = 'pu_vendid,pu_vendcode,pu_vendname,pu_currency,pu_rate,pu_payments,pu_transport,pu_suredate';
				tablename = 'Purchase';
				myfield = 'pu_code';
				break;
			case 'ProdInOut!DefectOut': //不良品出库单
				field = "pu_vendcode";
				tFields = 'pi_cardid,pi_cardcode,pi_title,pi_currency,pi_rate,pi_payment,pi_transport,pi_paydate';
				fields = 'pu_vendid,pu_vendcode,pu_vendname,pu_currency,pu_rate,pu_payments,pu_transport,pu_suredate';
				tablename = 'Purchase';
				myfield = 'pu_code';
				break;
			case 'ProdInOut!OutsidePicking': //委外领料单
				field = "ma_vendcode";
				tFields = 'pi_cardcode,pi_title,pi_departmentcode,pi_departmentname';
				fields = 'ma_vendcode,ma_vendname,ma_departmentcode,ma_departmentname';
				tablename = 'Make';
				myfield = 'ma_code';
				break;
			case 'ProdInOut!OutsideReturn': //委外退料单
				field = "ma_vendcode";
				tFields = 'pi_cardcode,pi_title,pi_departmentcode,pi_departmentname';
				fields = 'ma_vendcode,ma_vendname,ma_departmentcode,ma_departmentname';
				tablename = 'Make';
				myfield = 'ma_code';
				break;
			case 'ProdInOut!OutsideCheckIn': //委外验收单
				field = "ma_vendcode";
				tFields = 'pi_cardcode,pi_title,pi_departmentcode,pi_departmentname';
				fields = 'ma_vendcode,ma_vendname,ma_departmentcode,ma_departmentname';
				tablename = 'Make';
				myfield = 'ma_code';
				break;
			case 'ProdInOut!OutesideCheckReturn': //委外验收单
				field = "ma_vendcode";
				tFields = 'pi_cardcode,pi_title,pi_departmentcode,pi_departmentname';
				fields = 'ma_vendcode,ma_vendname,ma_departmentcode,ma_departmentname';
				tablename = 'Make';
				myfield = 'ma_code';
				break;
		}
		var obj = new Object();
		obj.field = field;
		obj.fields = fields;
		obj.tFields = tFields;
		obj.tablename = tablename;
		obj.myfield = myfield;
		return obj;
	},
	/**
	 * 有来源不能新增明细
	 */
	hasSource: function(grid) {
		var bool = false,field = null;
		switch(caller) {
			case 'ProdInOut!DrpSale'://配货单
				field = 'pd_snid';
				break;
			case 'ProdInOut!PurcCheckin': //采购验收单
				field = 'pd_qcid';
				break;
			case 'ProdInOut!DefectIn': //不良品入库单
				field = 'pd_qcid';
				break;
		}
		if(field != null) {
			var s = null;
			grid.store.each(function(item){
				s = item.get(field);
				if(s != null && s != '' && s > 0) {
					bool = true;return;
				}
			});
		}
		return bool;
	},
	isAllowUpdateQty: function(record) {
		var bool = true;
		switch(caller) {
			case 'ProdInOut!PurcCheckin': //采购验收单
				if(record.get('pd_qcid') != null && record.get('pd_qcid') > 0)
					bool = false;
				break;
			case 'ProdInOut!DefectIn': //不良品入库单
				if(record.get('pd_qcid') != null && record.get('pd_qcid') > 0)
					bool = false;
				break;
		}
		return bool;
	}
});