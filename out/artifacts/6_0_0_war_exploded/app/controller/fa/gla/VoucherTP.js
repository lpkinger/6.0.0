Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.VoucherTP', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'fa.gla.VoucherTP','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
    		'core.button.Add','core.button.Save','core.button.Close', 'core.button.Update', 'core.button.VoCreate',
    		'core.button.Delete','core.form.YnField','core.button.DeleteDetail', 'core.button.ExportExcelButton',
    		'core.trigger.DbfindTrigger','core.grid.YnColumn','core.form.YnField', 'core.trigger.CateTreeDbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'#vo_id': {
    			afterrender: function(f) {
    				var id = f.getValue();
    				if (Ext.isEmpty(id) || id == 0) {
    					me.getMonth();
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
    				this.beforeSave();
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.beforeUpdate();
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('vo_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				var tab = me.FormUtil.getActiveTab();
    				me.FormUtil.onAdd(null, '新增凭证模板', 'jsps/fa/gla/vouchertp.jsp');
    				setTimeout(function(){
        				if(tab) {
        					tab.close();
        				}
    				}, 200);
    			}
    		},
    		'erpVoCreateButton': {
    			click: function() {
    				me.onVoCreate(Ext.getCmp('vo_id').value);
    			}
    		},
    		'erpDeleteDetailButton': {
    			afterrender: function(btn){
    				//将当前行的 借方/贷方 以及外币借方/外币贷方 互换
    				btn.ownerCt.add({
    					text: '借贷调换',
    					width: 85,
    					disabled: true,
    			    	cls: 'x-btn-gray',
    			    	id: 'replace'
    				});
    				//当前行的借方 = 其它行的贷方总额-其它行的借方总额
    				btn.ownerCt.add({
    					text: '找平',
    					width: 65,
    					disabled: true,
    			    	cls: 'x-btn-gray',
    			    	id: 'level'
    				});
    			}
    		},
    		'erpGridPanel2': {
    			afterrender: function(grid){
    				var f = Ext.getCmp('vo_currencytype');
    				if(f) {
    					me.changeCurrencyType(f);
    				}
    				Ext.defer(function(){
    					Ext.EventManager.addListener(document.body, 'keydown', function(e){
        					if(e.getKey() == 187 && ['vd_debit', 'vd_credit'].indexOf(e.target.name) > -1) {
        						me.levelOut(e.target);
        					}
        				});
    				}, 200);
    			},
    			storeloaded: function(grid){
    				var f = Ext.getCmp('vo_currencytype');
    				if(f) {
    					me.changeCurrencyType(f);
    				}
    			},
    			itemclick: function(selModel, record){
    				var grid = selModel.ownerCt;
    				if(!grid.readOnly) {
    					this.GridUtil.onGridItemClick(selModel, record);
        				var btn = Ext.getCmp('replace');
        				btn.setDisabled(false);
        				btn = Ext.getCmp('level');
        				btn.setDisabled(false);
    				}
    			}
    		},
    		'field[name=vo_currencytype]': {
    			change: function(c){
    				me.changeCurrencyType(c);
    			}
    		},
    		/**
    		 * 借调互换
    		 */
    		'button[id=replace]': {
    			click: function(btn){
    				var grid = btn.ownerCt.ownerCt;
    				var record = grid.selModel.lastSelected;
    				if(record){
    					var v1 = record.data['vd_debit'];//借方
    					var v2 = record.data['vd_credit'];//贷方
    					var v3 = record.data['vd_doubledebit'];//原币借方
    					var v4 = record.data['vd_doublecredit'];//原币贷方
    					record.set('vd_debit', v2);
    					record.set('vd_credit', v1);
    					record.set('vd_doubledebit', v4);
    					record.set('vd_doublecredit', v3);
    				}
    			}
    		},
    		/**
    		 * 找平
    		 */
    		'button[id=level]': {
    			click: me.levelOut
    		},
    		'field[name=vd_doubledebit]': {//原币借方
    			focus : function(f) {
    				var grid = Ext.getCmp('grid'),
    					record = grid.selModel.lastSelected,
    					val = record.get('vd_doublecredit');
    				if( val != 0 ) {
    					f.setReadOnly(true);
    				} else {
    					f.setReadOnly(false);
    				}
    			},
    			change: function(f){
    				if(!f.ownerCt && f.value != null && f.value != 0 ){
    					var grid = Ext.getCmp('grid');
    					var record = grid.selModel.lastSelected,
    						rate = record.data['vd_rate'];
    					if(rate != null && rate > 0){
    						var val = Number((f.value*rate).toFixed(2));
    						if(record.data['vd_debit'] != val) {
    							record.set('vd_debit', val);//本币
    						}
    					}
    				}
    			}
    		},
    		'field[name=vd_doublecredit]': {//原币贷方
    			focus : function(f) {
    				var grid = Ext.getCmp('grid'),
    					record = grid.selModel.lastSelected,
    					val = record.get('vd_doubledebit');
    				if( val != 0 ) {
    					f.setReadOnly(true);
    				} else {
    					f.setReadOnly(false);
    				}
    			},
    			change: function(f){
    				if(!f.ownerCt && f.value != null && f.value != 0 ){
    					var record = Ext.getCmp('grid').selModel.lastSelected,
    						rate = record.data['vd_rate'];
    					if(rate != null && rate > 0){
    						var val = Number((f.value*rate).toFixed(2));
    						if(record.data['vd_credit'] != val) {
    							record.set('vd_credit', val);//本币
    						}
    					}
    				}
    			}
    		},
    		'field[name=vd_currency]': {
    			aftertrigger: function(f){
    				if(f.value != null && f.value != '' ){
    					var record = Ext.getCmp('grid').selModel.lastSelected;
    					if(record.data['vd_rate'] != null && record.data['vd_rate'] > 0){
    						if(record.data['vd_doubledebit'] != null){
            					record.set('vd_debit', 
            							(record.data['vd_doubledebit']*record.data['vd_rate']).toFixed(2));//原币计算本币
            				}
            				if(record.data['vd_doublecredit'] != null){
            					record.set('vd_credit', 
            							(record.data['vd_doublecredit']*record.data['vd_rate']).toFixed(2));//原币计算本币
            				}
    					}
    				}
    			}
    		},
    		'field[name=vd_explanation]': {
    			specialkey: function(f, e){//按ENTER自动把摘要复制到下一行
    				if (e.getKey() == e.ENTER) {
    					if(f.value != null && f.value != '' ){
        					var grid = Ext.getCmp('grid'),
        						record = grid.selModel.lastSelected,
        						idx = grid.store.indexOf(record),
        						next = grid.store.getAt(idx + 1);
        					if(next) {
        						var v = next.get('vd_explanation');
        						if(Ext.isEmpty(v))
        							next.set('vd_explanation', f.value);
        					}
        				}
    				}
    			},
    			change: function(f) {
    				if(f.value == '=') {
    					var grid = Ext.getCmp('grid'),
							record = grid.selModel.lastSelected,
							idx = grid.store.indexOf(record),
							prev = grid.store.getAt(idx - 1);
    					if(prev) {
    						var v = prev.get('vd_explanation');
    						if(!Ext.isEmpty(v))
    							f.setValue(v);
    					}
    				}
    			}
    		},
    		'field[name=vd_debit]': {
    			focus : function(f) {
    				var grid = Ext.getCmp('grid'),
    					record = grid.selModel.lastSelected,
    					val = record.get('vd_credit');
    				if( val != 0 ) {
    					f.setReadOnly(true);
    				} else {
    					f.setReadOnly(false);
    				}
    			},
    			specialkey: function(f, e){//按ENTER自动把摘要复制到下一行
    				if (e.getKey() == e.ENTER) {
    					var grid = Ext.getCmp('grid'),
							record = grid.selModel.lastSelected,
							val = record.get('vd_explanation'),
							idx = grid.store.indexOf(record),
							next = grid.store.getAt(idx + 1);
    					if(!Ext.isEmpty(val)) {
    						if(next) {
    							var v = next.get('vd_explanation');
    							if(Ext.isEmpty(v))
    								next.set('vd_explanation', val);
    						}
    					}
    				}
    			}
    		},
    		'field[name=vd_credit]': {
    			focus : function(f) {
    				var grid = Ext.getCmp('grid'),
    					record = grid.selModel.lastSelected,
    					val = record.get('vd_debit');
    				if( val != 0 ) {
    					f.setReadOnly(true);
    				} else {
    					f.setReadOnly(false);
    				}
    			},
    			specialkey: function(f, e){//按ENTER自动把摘要复制到下一行
    				if (e.getKey() == e.ENTER) {
    					var grid = Ext.getCmp('grid'),
							record = grid.selModel.lastSelected,
							val = record.get('vd_explanation'),
							idx = grid.store.indexOf(record),
							next = grid.store.getAt(idx + 1);
    					if(!Ext.isEmpty(val)) {
    						if(next) {
    							var v = next.get('vd_explanation');
    							if(Ext.isEmpty(v))
    								next.set('vd_explanation', val);
    						}
    					}
    				}
    			}
    		},
    		'dbfindtrigger[name=vd_catecode]': {
    			aftertrigger: function(f){
    				var grid = Ext.getCmp('grid'),
    					record = grid.selModel.lastSelected;
    				var type = record.get('ca_assname'), ass = record.get('ass') || [];
    				if(!Ext.isEmpty(type)){
    					var oldType = Ext.Array.concate(ass, '#', 'vds_asstype');
    					if(type != oldType) {
    						var idx = me.getRecordIndex(grid, record), dd = [];
        					Ext.Array.each(type.split('#'), function(t){
        						dd.push({
        							vds_vdid: idx,
        							vds_asstype: t
        						});
        					});
        					record.set('ass', dd);
    					}
    				} else
    					record.set('ass', null);
    			}
    		},
    		'cateTreeDbfindTrigger[name=vd_catecode]': {
    			aftertrigger: function(f, d){
    				var grid = Ext.getCmp('grid'),
						record = grid.selModel.lastSelected;
					var type = record.get('ca_assname'), ass = record.get('ass') || [];
					if(!Ext.isEmpty(type)){
						var oldType = Ext.Array.concate(ass, '#', 'vds_asstype');
						if(type != oldType) {
							var idx = me.getRecordIndex(grid, record), dd = [];
	    					Ext.Array.each(type.split('#'), function(t){
	    						dd.push({
	    							vds_vdid: idx,
	    							vds_asstype: t
	    						});
	    					});
	    					record.set('ass', dd);
						}
					} else
						record.set('ass', null);
    			},
    			afterrender: function(f){
    				f.onTriggerClick = function(){
    					me.showCateTree(f);
    				};
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getRecordIndex: function(grid, record) {
		var me = this, id = record.get(grid.keyField);
		if(!id || id == 0) {
			me.rowCounter = me.rowCounter || 0;
			id = --me.rowCounter;
			record.set(grid.keyField, id);
		}
		return id;
	},
	changeCurrencyType: function(c){
		var grid = Ext.getCmp('grid');
		if(grid) {
			var cols = grid.headerCt.getGridColumns();
			if(c.checked){
				Ext.each(cols, function(cn){
					if(cn.dataIndex == 'vd_doubledebit' || cn.dataIndex == 'vd_doublecredit'){
						cn.width = 110;
						cn.setVisible(true);
					}
					if(cn.dataIndex == 'vd_currency' || cn.dataIndex == 'vd_rate'){
						cn.width = 60;
						cn.setVisible(true);
					}
					if(cn.dataIndex == 'vd_debit'){
						cn.setText('本币借方');
					}
					if(cn.dataIndex == 'vd_credit'){
						cn.setText('本币贷方');
					}
				});
			} else {
				Ext.each(cols, function(cn){
					if(cn.dataIndex == 'vd_currency' || cn.dataIndex == 'vd_rate'
						|| cn.dataIndex == 'vd_doubledebit' || cn.dataIndex == 'vd_doublecredit'){
						cn.setVisible(false);
					}
					if(cn.dataIndex == 'vd_debit'){
						cn.setText('借方');
					}
					if(cn.dataIndex == 'vd_credit'){
						cn.setText('贷方');
					}
				});
			}	
		}
	},
	beforeSave: function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		var detail = Ext.getCmp('grid');
		var ass = [];
		detail.store.each(function(record){
			if(record.get('ca_assname')) {
				var s = record.get('ass') || [];
				Ext.Array.each(s, function(t, i){
					t.vds_id = t.vds_id || 0;
					t.vds_detno = i + 1;
					t.vds_vdid = String(t.vds_vdid);
					ass.push(t);
				});
			}
		});
		var param2 = Ext.encode(ass);
		Ext.each(detail.store.data.items, function(item, idx){
			if(item.data.vd_id == null || item.data.vd_id == 0){
				item.data.vd_id = -idx;
			}
		});
		var param1 = me.GridUtil.getGridStore(detail);
		if(detail.necessaryField.length > 0 && (param1.length == 0)){
			showError($I18N.common.grid.emptyDetail);
			return;
		}
		var ex = new Array(),d;
		Ext.each(param1, function(){//摘要未填写
			d = Ext.decode(this);
			if(Ext.isEmpty(d.vd_explanation)) {
				ex.push(d.vd_detno);
			}
		});
		if(ex.length > 0) {
			warnMsg("摘要未填写，序号:" + ex.join(',') + " 是否继续保存?", function(btn){
				if(btn == 'yes') {
					me.onSave(form, param1, param2);;
				}
			});
		} else {
			me.onSave(form, param1, param2);
		}
	},
	onSave: function(form, param1, param2) {
		var me = this;
		param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
		param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
		if(form.getForm().isValid()){
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
			r.vo_currencytype = Ext.getCmp('vo_currencytype').value ? -1 : 0;
			me.FormUtil.save(r, param1, param2);
		}else{
			me.FormUtil.checkForm();
		}
	},
	beforeUpdate: function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		var detail = Ext.getCmp('grid');
		var ass = [];
		detail.store.each(function(record){
			if(record.get('ca_assname')) {
				var s = record.get('ass') || [];
				Ext.Array.each(s, function(t, i){
					t.vds_id = t.vds_id || 0;
					t.vds_detno = i + 1;
					t.vds_vdid = String(t.vds_vdid);
					ass.push(t);
				});
			}
		});
		var param1 = me.GridUtil.getGridStore(detail);
		var param2 = Ext.encode(ass);
		if(me.FormUtil.checkFormDirty(form) == '' && detail.necessaryField.length > 0 && (param1.length == 0) && (param2.length <= 2)){
			showError($I18N.common.grid.emptyDetail);
			return;
		} else {
			param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
			param2 = param2 == null ? [] : param2.toString().replace(/\\/g,"%");
			if(form.getForm().isValid()){
				Ext.each(form.items.items, function(item){
					if(item.xtype == 'numberfield'){
						if(item.value == null || item.value == ''){
							item.setValue(0);
						}
					}
				});
				var r = form.getValues();
				r.vo_currencytype = Ext.getCmp('vo_currencytype').value ? -1 : 0;
				me.FormUtil.update(r, param1, param2);
			}else{
				me.FormUtil.checkForm();
			}
		}
	},
	levelOut:function (target){
		var grid = Ext.getCmp('grid');
		var record = grid.selModel.lastSelected;
		if(record){
			var f = Ext.getCmp('vo_currencytype');
			var debit = 0;
			var credit = 0;
			var rate = record.get('vd_rate');
			rate = rate == 0 ? 1 : rate;
			grid.getStore().each(function(item){
				if(item.id != record.id){
					debit += item.get('vd_debit');
					credit += item.get('vd_credit');
				}
			});
			var targetName = target.name;
			if(record.get('vd_debit') != 0)
				targetName = 'vd_debit';
			else if(record.get('vd_credit') != 0)
				targetName = 'vd_credit';
			if(targetName && typeof targetName == 'string') {
				if(targetName == 'vd_debit') {
					debit = credit - debit;
					record.set('vd_debit', debit.toFixed(4));
					if(f.checked) {
						record.set('vd_doubledebit', Number((debit/rate).toFixed(4)));
					}
				} else if(targetName == 'vd_credit'){
					credit = debit - credit;
					record.set('vd_credit', credit.toFixed(4));
					if(f.checked) {
						record.set('vd_doublecredit', Number((credit/rate).toFixed(4)));
					}
				}
				if(target.name == targetName)
					target.value = record.get(targetName);
			} else {
				if(credit > debit) {
					record.set('vd_debit', credit - debit);
					if(f.checked) {
						record.set('vd_doubledebit', Number(((credit - debit)/rate).toFixed(4)));
					}
				} else {
					record.set('vd_credit', debit - credit);
					if(f.checked) {
						record.set('vd_doublecredit', Number(((debit - credit)/rate).toFixed(4)));
					}
				}
			}
		}
	},
	showCateTree: function(f) {
		var cawin = Ext.getCmp('cawin');
		if(!cawin) {
			cawin = new Ext.window.Window({
	    		id : 'cawin',
			    title: '科目查找',
			    height: "100%",
			    width: "80%",
			    maximizable : true,
				buttonAlign : 'center',
				layout : 'anchor',
				modal:true,
			    items: [{
			    	tag : 'iframe',
			    	frame : true,
			    	anchor : '100% 100%',
			    	layout : 'fit',
			    	html : '<iframe id="iframe_dbfind_'+caller+"_"+f.name+"="+f.value+'" src="'+basePath+'jsps/common/catetreepaneldbfind.jsp?key='+f.name+"&dbfind=&caller1="+caller+"&keyValue="+f.value+"&trigger="+f.id+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			    }],
			    buttons : [{
			    	text : '确  认',
			    	iconCls: 'x-button-icon-save',
			    	cls: 'x-btn-gray',
			    	handler : function(btn){
			    		var contentwindow = Ext.getCmp('cawin').body.dom.getElementsByTagName('iframe')[0].contentWindow;
			    		var tree = contentwindow.Ext.getCmp('tree-panel');
			    		var data = tree.getChecked(), record = Ext.getCmp('grid').selModel.lastSelected;
			    		var dbfinds = Ext.getCmp('grid').dbfinds;
			    		if(dbfinds != null && record){
			    			Ext.each(dbfinds, function(dbfind,index){
			    				record.set(dbfind.field, data[0].raw.data[dbfind.dbGridField]);
				    		});
			    		}
			    		f.fireEvent('aftertrigger', f, data);
			    		btn.ownerCt.ownerCt.hide();
			    	}
			    },{
			    	text : '关  闭',
			    	iconCls: 'x-button-icon-close',
			    	cls: 'x-btn-gray',
			    	handler : function(btn){
			    		btn.ownerCt.ownerCt.hide();
			    	}
			    }]
			});
		}
		cawin.show();
	},
    getMonth: function() {
    	Ext.Ajax.request({
    		url: basePath + 'fa/getMonth.action',
    		params: {
    			votype: 'GL'
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				Ext.getCmp('vo_yearmonth').setValue(rs.data.PD_DETNO);
    				Ext.getCmp('vo_date').setValue(new Date(rs.data.PD_ENDDATE));
    			}
    		}
    	});
    },
    onVoCreate: function(id) {
    	Ext.Ajax.request({
    		url: basePath + 'fa/gla/createvobytp.action',
    		params: {
    			id: id
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				showMessage('提示', '制作成功！<a href="javascript:openUrl(\'jsps/fa/ars/voucher.jsp?formCondition=vo_idIS' + 
							 + rs.data.vo_id + '&gridCondition=vd_voidIS' + rs.data.vo_id + 
							'\')">\n凭证号:&lt;' + rs.data.vo_number + 
							'&gt;\n流水号:&lt;' + rs.data.vo_code + '&gt;</a>');
    			} else {
    				showError(rs.exceptionInfo);
    			}
    		}
    	});
    }
});