/**
 * 修改供应商UU按钮
 */
Ext.define('erp.view.core.button.VendorUU', {
	extend : 'Ext.Button',
	alias : 'widget.erpVendorUUButton',
	iconCls : 'x-btn-uu-medium',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpVendorUUButton,
	style : {
		marginLeft : '10px'
	},
	width : 120,
	initComponent : function() {
		this.callParent(arguments);
	},
	listeners: {
		afterrender: function(btn) {
			var status = Ext.getCmp('ve_auditstatuscode');
			if(status && status.value == 'ENTERING'){
				btn.hide();
			}
		}
	},
	handler: function() {
		var me = this, win = Ext.getCmp('vendoruu-win');
		if(!win) {
			var f = Ext.getCmp('ve_uu'), a = Ext.getCmp('ve_ifdeliveryonb2b'),b=Ext.getCmp('ve_b2bcheck'),x=Ext.getCmp('ve_webserver'),y=Ext.getCmp('ve_legalman'),z=Ext.getCmp('ve_add1');
				id = Ext.getCmp('ve_id').getValue(), uu = f ? f.value : null, dev = a ? a.value : '',check = b ? b.value : '',ve_webserver = x?x.value:'',ve_legalman=y?y.value:'',ve_add1=z?z.value:'';
			me.isEnable(id, function(enable){
				var items = [me.createCheckForm(uu, dev, check,enable,ve_webserver,ve_legalman,ve_add1)];
				if(!enable)
					items.push(me.createCheckGrid());
				win = Ext.create('Ext.Window', {
					id: 'vendoruu-win',
					title: '设置供应商 ' + Ext.getCmp('ve_code').value + ' 的UU号',
					width: '80%',
					items: items,
					closeAction: 'hide',
					modal: true,
					autoShow: true
				});
				if(!enable)
					me.check(uu);
			});
		} else
			win.show();
	},
	createCheckForm: function(uu, dev,check, enable,ve_webserver,ve_legalman,ve_add1) {
		var me = this;
		return new Ext.form.Panel({
			xtype: 'form',
			height: 120,
			bodyStyle: 'background:#f1f2f5;',
			layout: 'column',
			fieldDefaults: {
				margin: '5',
				columnWidth:0.25,
			},
			items: [{
				xtype: 'erpYnField',
				name:'ve_ifdeliveryonb2b',
				fieldLabel: '<font color=red>启用B2B收料</font>',
				value: dev,
				allowBlank: false,
				flex: 2.5
			},{
				xtype: 'erpYnField',
				name:'ve_b2bcheck',
				fieldLabel: '<font color=red>启用B2B对账</font>',
				value: check,
				allowBlank: false,
				flex: 2.5
			},{
				xtype : 'textfield',
				name : 've_webserver',
				fieldLabel: '营业执照',
				hideTrigger: true,
				readOnly: true,
				value: ve_webserver,
			},{
				xtype : 'textfield',
				name : 've_legalman',
				fieldLabel: '法定代表人',
				hideTrigger: true,
				readOnly: true,
				value: ve_legalman,
			},{
				xtype : 'textfield',
				name : 've_add1',
				fieldLabel: '企业地址',
				hideTrigger: true,
				readOnly: true,
				value: ve_add1,
			},{
				xtype: 'numberfield',
				name:'ve_uu',
				fieldLabel: '供应商UU号',
				hideTrigger: true,
				//readOnly: true,
				value: uu,
				flex: 3
			},{
				xtype: 'displayfield',
				flex: 3,
				id: 'b2benable',
				fieldCls: 'x-form-field-help',
				checkedIcon: 'x-button-icon-agree',
				uncheckedIcon: 'x-button-icon-unagree',
				value: (enable ? '<i class="x-button-icon-agree"></i>验证通过' : null),
				setChecked: function(checked) {
					this.checked = checked;
					this.setValue(('<i class="' + (checked ? this.checkedIcon : this.uncheckedIcon) + '"></i>') + 
							(checked ? '验证通过,请保存' : '验证失败'));
				}
			}],
			buttonAlign: 'center',
			buttons: [{
				text: '检测',
				cls: 'x-btn-gray',
				hidden: enable,
				handler: function(btn) {
					var form = btn.ownerCt.ownerCt,
						a = form.down('numberfield[name=ve_uu]');
					if(a.value) {
						me.check(a.value);
					} else
						form.down('#b2benable').setChecked(false);
				}
			}, {
				text: $I18N.common.button.erpSaveButton,
				cls: 'x-btn-gray',
				handler: function(btn) {
					var form = btn.ownerCt.ownerCt,
						a = form.down('numberfield[name=ve_uu]'),
						b = form.down('field[name=ve_ifdeliveryonb2b]');
						c = form.down('field[name=ve_b2bcheck]');
					var d = form.down('field[name=ve_webserver]');
					var e = form.down('field[name=ve_legalman]');
					var f = form.down('field[name=ve_add1]');
					if (a.value) {
						me.onConfirm(Ext.getCmp('ve_id').value, a.value, b.value, c.value, d.value, e.value, f.value);
					}
				}
			}, {
				text: $I18N.common.button.erpCloseButton,
				cls: 'x-btn-gray',
				handler: function(btn) {
					btn.up('window').hide();
				}
			}]
		});
	},
	createCheckGrid: function() {
		var me = this;
		return new Ext.grid.Panel({
			xtype: 'grid',
			title: '查找平台注册企业资料',
			height: 400,
			columnLines: true,
			columns: [{
				text: '企业名称',
				dataIndex: 'name',
				cls: 'x-grid-header-1',
				flex: 5,
				renderer: function(val, meta, record, x, y, store, view) {
					var c = view.ownerCt.searchConfig;
					if(c && c.name) {
						if(val.length > c.name.length)
							val = val.replace(c.name, '<font style="color:#c00">' + c.name + '</font>');
						else if(c.name.indexOf(val) > -1)
							val = '<font style="color:#c00">' + val + '</font>';
					}
					return val;
				}
			},{
				text: '简称',
				dataIndex: 'shortName',
				cls: 'x-grid-header-1',
				flex: 2,
				renderer: function(val, meta, record, x, y, store, view) {
					var c = view.ownerCt.searchConfig;
					if(c && c.shortName) {
						if(val.length > c.shortName.length)
							val = val.replace(c.shortName, '<font style="color:#c00">' + c.shortName + '</font>');
						else if(c.shortName.indexOf(val) > -1)
							val = '<font style="color:#c00">' + val + '</font>';
					}
					return val;
				}
			},{
				text: '营业执照',
				dataIndex: 'businessCode',
				flex: 2,
				cls: 'x-grid-header-1',
				align: 'right',
				renderer: function(val, meta, record, x, y, store, view) {
					var c = view.ownerCt.searchConfig;
					if(c && c.businessCode && val == c.businessCode) {
						val = '<font style="color:#c00">' + val + '</font>';
					}
					return val;
				}
			},{
				text: '法定代表人',
				dataIndex: 'corporation',
				flex: 2,
				cls: 'x-grid-header-1',
				align: 'center',
				renderer: function(val, meta, record, x, y, store, view) {
					var c = view.ownerCt.searchConfig;
					if(c && c.corporation && val == c.corporation) {
						val = '<font style="color:#c00">' + val + '</font>';
					}
					return val;
				}
			},{
				text: '地址',
				dataIndex: 'address',
				cls: 'x-grid-header-1',
				flex: 5
			},{
				text: 'UU',
				xtype: 'numbercolumn',
				dataIndex: 'uu',
				flex: 2,
				cls: 'x-grid-header-1',
				align: 'right',
				renderer: function(val, meta, record, x, y, store, view) {
					var c = view.ownerCt.searchConfig;
					if(c && c.uu && val == c.uu) {
						val = '<font style="color:#c00">' + val + '</font>';
					}
					return val;
				}
			},{
				text: '操作',
				xtype:'actioncolumn',
				cls: 'x-grid-header-1',
				flex: 1,
				align: 'center',
				items: [{
	                icon: basePath + 'resource/images/32/select.png', 
	                tooltip: '选择',
	                handler: function(grid, rowIndex, colIndex) {
	                	me.selectRecord(grid, grid.getStore().getAt(rowIndex));
	                }
	            }]
			}],
			features : [Ext.create('Ext.grid.feature.Grouping',{
		        groupHeaderTpl: '按{name}查找到的全部可能结果：'
		    })],
		    store: new Ext.data.Store({
		    	fields: ['group','name','shortName','uu','businessCode','corporation','address'],
		    	groupField: 'group'
		    }),
		    html: '<p class="x-grid-empty alert">没有查找到匹配的企业，您的供应商还未注册<a href="http://www.ubtob.com" target="_blank">优软商务平台</a>或您填写的供应商资料有误，请联系供应商确认！</p>' +
		    	'<p class="x-grid-tip alert arrow-border arrow-bottom-right">从查找到的结果中选择正确的供应商信息<a href="javascript:void(0);" class="pull-right close">知道了&times;</a></p>',
		    checkEmpty: function(checked) {
		    	var empEl = this.getEl().select('.x-grid-empty'), tipEl = this.getEl().select('.x-grid-tip');
		    	empEl.applyStyles({'display': this.store.getCount() == 0 ? 'block' : 'none'});
		    	tipEl.applyStyles({'display': !checked && this.store.getCount() > 0 ? 'block' : 'none'});
		    	if(!checked && this.store.getCount() > 0) {
		    		Ext.EventManager.on(tipEl.el.dom.childNodes[1], {
		    			click: function(event, el) {
		    				tipEl.applyStyles({'display': 'none'});
		    				Ext.EventManager.stopEvent(event);
		    			},
		    			buffer: 50
					});
		    	}
		    }
		});
	},
	onConfirm: function(veId, uu, dev,check,ve_webserver,ve_legalman,ve_add1) {
		var me = this, grid = Ext.getCmp('vendoruu-win').down('grid');
		if(!grid)
			me.updateVendorUU(veId, uu, null, null, dev,check, 1,ve_webserver,ve_legalman,ve_add1);
		else {
			if(me.selectedConfig && me.selectedConfig.uu == uu)
				me.updateVendorUU(veId, uu, me.selectedConfig.name, me.selectedConfig.shortName, dev,check, 1,ve_webserver,ve_legalman,ve_add1);
			else
				me.check(uu, function(checked){
					if(checked)
						me.updateVendorUU(veId, uu, null, null, dev,check, 1,ve_webserver,ve_legalman,ve_add1);
					else
						warnMsg('UU号还未验证通过，是否继续保存？', function(btn){
							if(btn == 'yes')
								me.updateVendorUU(veId, uu, null, null, dev,check, 0,ve_webserver,ve_legalman,ve_add1);
						});
				});	
		}
	},
	updateVendorUU: function(veId, uu, name, shortName, dev,check, checked,ve_webserver,ve_legalman,ve_add1) {
		if(checked==0 && (dev!=0 || check!=0)){
			showError("当前供应商未开通B2B，不允许启用B2B收料和B2B对账！");
			return;
		}
		Ext.Ajax.request({
			url: basePath + 'scm/vendor/updateUU.action',
			params: {
				id: veId,
				uu: uu,
				name: name,
				shortName: shortName,
				isb2b: dev,
				b2bcheck:check,
				checked: checked,
				ve_webserver : ve_webserver,
				ve_legalman : ve_legalman,
				ve_add1 : ve_add1
			},
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else {
					alert('设置成功!');
					window.location.reload();
				}
			}
		});
	},
	check: function(uu, callback) {
		var name = Ext.getCmp('ve_name').getValue(), sf = Ext.getCmp('ve_shortname'), shortName = sf ? sf.getValue() : null;
		var grid = Ext.getCmp('vendoruu-win').down('grid'), me = this;
		if(grid) {
			grid.setLoading(true);
			this.getMembers(name, shortName, uu, function(data){
				var s = [], checked = false;
				if(data) {
					for(var k in data) {
						Ext.Array.each(data[k], function(d){
							d.group = k == 'name' ? ('名称(' + name + ')') : (k == 'shortName' ? 
									('简称(' + shortName + ')') : ('UU号(' + uu + ')'));
							s.push(d);
						});
						if(!checked && k == 'uu' && data[k])
							checked = true;
					}
				}
				grid.searchConfig = {name: name, shortName: shortName, uu: uu};
				me.selectedConfig = null;
				grid.store.loadData(s);
				grid.checkEmpty(checked);
				Ext.getCmp('b2benable').setChecked(checked);
				grid.setLoading(false);
				callback && callback.call(null, checked);
			});
		}
	},
	isEnable: function(veId, callback) {
		Ext.Ajax.request({
			url : basePath + 'common/getFieldData.action',
			params: {
				caller: 'Vendor',
				field: 've_b2benable',
				condition: 've_id=' + veId
			},
			method : 'post',
			callback : function(opt, s, res){
				var r = new Ext.decode(res.responseText);
				if(r.exceptionInfo){
					showError(r.exceptionInfo);
				}
				callback && callback.call(null, (!r.data || r.data != 1) ? false : true);
			}
		});
	},
	getMembers: function(name, shortName, uu, callback) {
		Ext.Ajax.request({
			url: basePath + 'b2b/queriable/members.action',
			params: {
				name: name,
				shortName: shortName,
				uu: uu
			},
			method: 'GET',
			callback: function(opt, s, r) {
				var d = [];
				if(r && r.responseText) {
					var rs = Ext.decode(r.responseText);
					if(rs.exceptionInfo) {
						showError(rs.exceptionInfo);
					} else
						d = rs;
				}console.log(d);
				callback.call(null, d);
			}
		});
	},
	selectRecord: function(grid, record) {
		var win = grid.up('window'), u = win.down('field[name=ve_uu]'), e = win.down('#b2benable'),x = win.down('field[name=ve_webserver]'), y = win.down('field[name=ve_legalman]'),z = win.down('field[name=ve_add1]'),
			_n = Ext.getCmp('ve_name').getValue(), sf = Ext.getCmp('ve_shortname'), _t = sf ? sf.getValue() : null, me = this;
	    u.setValue(record.get('uu'));
	    x.setValue(record.get('businessCode'));
	    y.setValue(record.get('corporation'));
	    z.setValue(record.get('address'));
	    e.setChecked(true);
	    me.selectedConfig = {uu: record.get('uu')};
	    var s = '', n = record.get('name'), t = record.get('shortName');
    	if(_n) {
    		if(_n != n)
    			s = '供应商名称【' + _n + '】与您选择的企业注册信息【' + n + '】不一致，是否按平台的企业注册信息修改？<br>';
    	} else {
    		me.selectedConfig.name = n;// 为空时，直接写入
    	}
    	if(_t) {
    		if(_t != t)
    			s += '供应商简称【' + _t + '】与您选择的企业注册信息【' + t + '】不一致，是否按平台的企业注册信息修改？<br>';
    	} else {
    		me.selectedConfig.shortName = t;// 为空时，直接写入
    	}
    	if(s.length > 0)
        	warnMsg(s, function(btn){
        		if(btn == 'yes') {
        			me.selectedConfig.name = n;
        			me.selectedConfig.shortName = t;
        		}
			});
	}
});