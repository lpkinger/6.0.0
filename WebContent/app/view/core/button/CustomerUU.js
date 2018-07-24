/**
 * 修改客户UU按钮
 */
Ext.define('erp.view.core.button.CustomerUU', {
	extend : 'Ext.Button',
	alias : 'widget.erpCustomerUUButton',
	iconCls : 'x-btn-uu-medium',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpCustomerUUButton,
	style : {
		marginLeft : '10px'
	},
	width : 120,
	initComponent : function() {
		this.callParent(arguments);
	},
	listeners: {
		afterrender: function(btn) {
			var status = Ext.getCmp('cu_auditstatuscode');
			if(status && status.value == 'ENTERING'){
				btn.hide();
			}
		}
	},
	handler: function() {
		var me = this, win = Ext.getCmp('customeruu-win');
		if(!win) {
			var f = Ext.getCmp('cu_uu'), x=Ext.getCmp('cu_businesscode'),y=Ext.getCmp('cu_lawman'),z=Ext.getCmp('cu_add1')
				id = Ext.getCmp('cu_id').getValue(), uu = f ? f.value : null,cu_businesscode = x?x.value:'',cu_lawman=y?y.value:'',cu_add1=z?z.value:'';
			me.isEnable(id, function(enable){
				var items = [me.createCheckForm(uu,  enable,cu_businesscode,cu_lawman,cu_add1)];
				if(!enable)
					items.push(me.createCheckGrid());
				win = Ext.create('Ext.Window', {
					id: 'customeruu-win',
					title: '设置客户 ' + Ext.getCmp('cu_code').value + ' 的UU号',
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
	createCheckForm: function(uu, enable,cu_businesscode,cu_lawman,cu_add1) {
		var me = this;
		return new Ext.form.Panel({
			xtype: 'form',
			height: 120,
			bodyStyle: 'background:#f1f2f5;',
			layout: 'column',
			fieldDefaults: {
				margin: '8',
				columnWidth:0.3,
			},
			items: [{
				xtype : 'textfield',
				name : 'cu_businesscode',
				fieldLabel: '营业执照',
				hideTrigger: true,
				readOnly: true,
				value: cu_businesscode,
			},{
				xtype : 'textfield',
				name : 'cu_lawman',
				fieldLabel: '法定代表人',
				hideTrigger: true,
				readOnly: true,
				value: cu_lawman,
			},{
				xtype : 'textfield',
				name : 'cu_add1',
				fieldLabel: '企业地址',
				hideTrigger: true,
				readOnly: true,
				value: cu_add1,
			},{
				xtype: 'numberfield',
				name:'cu_uu',
				fieldLabel: '客户UU号',
				hideTrigger: true,
				readOnly: true,
				value: uu,
				flex: 3
			},{
				xtype: 'displayfield',
				flex: 4,
				id: 'b2benable',
				fieldCls: 'x-form-field-help',
				checkedIcon: 'x-button-icon-agree',
				uncheckedIcon: 'x-button-icon-unagree',
				value: (enable ? '<i class="x-button-icon-agree"></i>验证通过' : null),
				setChecked: function(checked) {
					this.checked = checked;
					this.setValue(('<i class="' + (checked ? this.checkedIcon : this.uncheckedIcon) + '"></i>') + 
							(checked ? '验证通过' : '验证失败'));
				}
			}],
			buttonAlign: 'center',
			buttons: [{
				text: '检测',
				cls: 'x-btn-gray',
				hidden: enable,
				handler: function(btn) {
					var form = btn.ownerCt.ownerCt,
						a = form.down('numberfield[name=cu_uu]');
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
						a = form.down('numberfield[name=cu_uu]');
					var d = form.down('field[name=cu_businesscode]');
					var e = form.down('field[name=cu_lawman]');
					var f = form.down('field[name=cu_add1]');	
					if(a.value) {
						me.onConfirm(Ext.getCmp('cu_id').value, a.value,d.value,e.value,f.value);
					} else
						form.down('#b2benable').setChecked(false);
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
				text: '营业执照号',
				dataIndex: 'businessCode',
				cls: 'x-grid-header-1',
				flex: 2,
			},{
				text: '法定代表人',
				dataIndex: 'corporation',
				cls: 'x-grid-header-1',
				flex: 2,
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
	onConfirm: function(veId, uu,cu_businesscode,cu_lawman,cu_add1) {
		var me = this, grid = Ext.getCmp('customeruu-win').down('grid');
		if(!grid)
			me.updateVendorUU(veId, uu, null, null, 1,cu_businesscode,cu_lawman,cu_add1);
		else {
			if(me.selectedConfig && me.selectedConfig.uu == uu)
				me.updateVendorUU(veId, uu, me.selectedConfig.name, me.selectedConfig.shortName, 1,cu_businesscode,cu_lawman,cu_add1);
			else
				me.check(uu, function(checked){
					if(checked)
						me.updateVendorUU(veId, uu, null, null, 1,cu_businesscode,cu_lawman,cu_add1);
					else
						warnMsg('UU号还未验证通过，是否继续保存？', function(btn){
							if(btn == 'yes')
								me.updateVendorUU(veId, uu, null, null, 0,cu_businesscode,cu_lawman,cu_add1);
						});
				});	
		}
	},
	updateVendorUU: function(veId, uu, name, shortName, checked,cu_businesscode,cu_lawman,cu_add1) {
		Ext.Ajax.request({
			url: basePath + 'scm/customer/updateUU.action',
			params: {
				id: veId,
				uu: uu,
				name: name,
				shortName: shortName,
				checked: checked,
				cu_businesscode : cu_businesscode,
				cu_lawman : cu_lawman,
				cu_add1 : cu_add1
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
		var name = Ext.getCmp('cu_name').getValue(), shortName = Ext.getCmp('cu_shortname').getValue();
		var grid = Ext.getCmp('customeruu-win').down('grid'), me = this;
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
				caller: 'Customer',
				field: 'cu_b2benable',
				condition: 'cu_id=' + veId
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
				}
				callback.call(null, d);
			}
		});
	},
	selectRecord: function(grid, record) {
		var win = grid.up('window'), u = win.down('field[name=cu_uu]'), e = win.down('#b2benable')
			_n = Ext.getCmp('cu_name').getValue(), _t = Ext.getCmp('cu_shortname').getValue(), me = this;
	    var x = win.down('field[name=cu_businesscode]'), y = win.down('field[name=cu_lawman]'),z = win.down('field[name=cu_add1]')
		u.setValue(record.get('uu'));
	    x.setValue(record.get('businessCode'));
	    y.setValue(record.get('corporation'));
	    z.setValue(record.get('address'));
	    e.setChecked(true);
	    me.selectedConfig = {uu: record.get('uu')};
	    var s = '', n = record.get('name'), t = record.get('shortName');
    	if(_n) {
    		if(_n != n)
    			s = '客户名称【' + _n + '】与您选择的企业注册信息【' + n + '】不一致，是否按平台的企业注册信息修改？<br>';
    	} else {
    		me.selectedConfig.name = n;// 为空时，直接写入
    	}
    	if(_t) {
    		if(_t != t)
    			s += '客户名称简称【' + _t + '】与您选择的企业注册信息【' + t + '】不一致，是否按平台的企业注册信息修改？<br>';
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