/**
 * excel导入Grid
 */
Ext.define('erp.view.core.button.UpExcel', {
	extend : 'Ext.form.Panel',
	alias : 'widget.upexcel',
	initComponent : function() {
		if (this.iconCls) {
			this.items[0].buttonConfig.iconCls = this.iconCls;
		}
		if (this.cls) {
			this.items[0].buttonConfig.cls = this.cls;
		}
		if (this.itemCls) {
			this.items[0].buttonConfig.cls = this.itemCls;
		}
		if (this.iconCls) {
			this.items[0].buttonConfig.text = this.text;
		}
		this.callParent(arguments);
	},
	bodyStyle : 'background: #fff;border: none;',
	items : [ {
		xtype : 'filefield',
		name : 'file',
		buttonOnly : true,
		hideLabel : true,
		width : 62,
		height : 26,
		buttonConfig : {
			style:{border:'0px',background:'#fff'},
			height : 26,
			iconCls : 'x-button-icon-excel',
			cls : 'x-btn-gray',
			text : $I18N.common.button.erpUpExcelButton,
			handler:function(btn,e){
				var grid = Ext.getCmp('grid');
				if(grid&&grid.deleteBeforeImport){
					for(var i=0;i<grid.getStore().getCount();i++){
						var record = grid.getStore().getAt(i);
						var keyValue = record.get(grid.keyField.toLocaleLowerCase());
						if(keyValue!=0 || keyValue!='' || keyValue!=null){
							Ext.Msg.show({ 
								title : '提示', 
								msg : '请先删除明细表的内容，才可以进行导入操作!', 
								buttons: Ext.Msg.OK 
							});
							e.preventDefault();
						}
					}
					return;        	
				}
			}
		},
		listeners : {
			change : function(field) {
				field.ownerCt.upexcel(field);
			}
		}
	} ],
	upexcel : function(field) {
		var bool = this.fireEvent('beforeimport', this);
		if (bool != false) {
			this.getForm().submit({
				url : basePath + 'common/upexcel.action',
				waitMsg : "正在解析Excel",
				success : function(fp,o) {
					field.reset();
					var columns = o.result.grid.gridColumns;
					var errColumns = field.ownerCt.checkErrorColumn(columns);
					if(errColumns){
						Ext.Msg.alert("提示","导入数据中有不在明细行数据的列:"+errColumns);
						return;
					}
					var data = Ext.decode(Ext.String.htmlDecode(o.result.grid.dataString.replace(/,}/g, '}').replace(/,]/g, ']')));
					field.ownerCt.createWin(columns, o.result.grid.gridFields, data);
				},
				failure : function(fp, o) {
					if (o.result.size) {
						showError(o.result.error + "&nbsp;" + Ext.util.Format.fileSize(o.result.size));
						field.reset();
					} else {
						showError(o.result.error);
						field.reset();
					}
				}
			});
		}
	},
	createWin : function(columns, fields, data) {
		var me = this;
		var form = me.createExcelForm(columns);
		var grid = me.createExcelGrid(columns, fields, data);
		Ext.create('Ext.window.Window', {
			id : 'excelwin',
			height : '100%',
			width : '90%',
			title : '请选择要导入的列和数据',
			layout : 'anchor',
			items : [ form, {
				xtype : 'radiogroup',
				columns : 2,
				margin : '0 0 0 10',
				anchor : '50% 3%',
				fieldLabel : '导入模式',
				vertical : true,
				items : [ {
					boxLabel : '追加',
					name : 'import_mode',
					inputValue : '+'
				}, {
					boxLabel : '替换',
					name : 'import_mode',
					inputValue : '-',
					checked : true
				} ]
			}, grid ],
			buttonAlign : 'center',
			buttons : [ {
				text : $I18N.common.button.erpConfirmButton,
				iconCls : 'x-button-icon-save',
				cls : 'x-btn-gray',
				id:'confirmimport',
				handler : function(btn) {
					me.exportGridToGrid(grid, me.grid || me.ownerCt.ownerCt || me.ownerCt.floatParent.ownerCt.ownerCt, function(){
						Ext.getCmp('excelwin').close();
						btn.fireEvent('afterimport',btn,grid);
					});
				}
			}, {
				text : $I18N.common.button.erpCloseButton,
				iconCls : 'x-button-icon-close',
				cls : 'x-btn-gray',
				handler : function() {
					Ext.getCmp('excelwin').close();
				}
			} ]
		}).show();
		grid.selModel.selectAll();
		Ext.each(form.items.items, function(item, index) {
			item.on('change', function() {
				Ext.each(grid.columns, function(c) {
					if (c.dataIndex == item.name) {
						if (item.checked) {
							c.show();
						} else {
							c.hide();
						}
					}
				});
			});
		});
	},
	checkErrorColumn:function(columns){
		var me = this;
		var res = null;
		var errColumns = new Array();
		var tGrid = me.grid || me.ownerCt.ownerCt || me.ownerCt.floatParent.ownerCt.ownerCt;
		var texts = me.getGridText(tGrid);
		Ext.each(columns, function(c, index) {
			var label = c.header || c.text;
			if (!Ext.Array.contains(texts, label)) {
				label = label.replace(/\s+/,'&nbsp;&nbsp;&nbsp;'); //空格处理
				errColumns.push('<span style="background:white"><font color="red">'+label+'</font></span>');
			}
		});
		if(errColumns.length>0){
			res = errColumns.join(",");
		}
		return res;
	},
	createExcelForm : function(columns) {
		var l = Math.floor(columns.length / 5);
		var h = 2 + l * 5;
		var items = new Array();
		var item = null;
		var tGrid = this.grid || this.ownerCt.ownerCt || this.ownerCt.floatParent.ownerCt.ownerCt;
		var texts = this.getGridText(tGrid);
		Ext.each(columns, function(c, index) {
			item = new Object();
			item.id = c.dataIndex;
			item.name = c.dataIndex;
			item.xtype = 'checkbox';
			var label = c.header || c.text;
			if (!Ext.Array.contains(texts, label)) {// 将tGrid里面没有的列，加特殊的样式
				label = "<s>" + label + "</s>";
				item.style = 'color:#B6B7F9';
			}
			item.boxLabel = label;
			item.checked = true;
			item.labelAlign = 'right';
			item.columnWidth = .2;
			items.push(item);
		});
		return Ext.create('Ext.form.Panel', {
			anchor : '100% ' + h + '%',
			layout : 'column',
			autoScroll:true,
			bodyStyle : 'background: #f1f1f1;padding: 10px;',
			items : items
		});
	},
	createExcelGrid : function(columns, fields, data) {
		var me = this;
		var cols = new Array();
		Ext.each(columns, function(c) {
			c = me.removeKeys(c, [ 'locked', 'summaryType', 'logic', 'renderer' ]);
			cols.push(c);
		});
		var l = Math.floor(cols.length / 5);
		var h = 95 - l * 5;
		return Ext.create('Ext.grid.Panel', {
			id : 'excelgrid',
			anchor : '100% ' + h + '%',
			columns : cols,
			store : Ext.create('Ext.data.Store', {
				fields : fields,
				data : data
			}),
			columnLines : true,
			multiselected : new Array(),
			selModel : Ext.create('Ext.selection.CheckboxModel', {
				ignoreRightMouseSelection : false,
				listeners : {
					selectionchange : function(selectionModel, selected, options) {

					}
				},
				onRowMouseDown : function(view, record, item, index, e) {
					var me = Ext.getCmp('excelgrid');
					var bool = true;
					var items = me.selModel.getSelection();
					Ext.each(items, function(item, index) {
						if (this.index == record.index) {
							bool = false;
							me.selModel.deselect(record);
							Ext.Array.remove(items, item);
							Ext.Array.remove(me.multiselected, record);
						}
					});
					Ext.each(me.multiselected, function(item, index) {
						items.push(item);
					});
					me.selModel.select(items);
					if (bool) {
						view.el.focus();
						var checkbox = item.childNodes[0].childNodes[0].childNodes[0];
						if (checkbox.getAttribute('class') == 'x-grid-row-checker') {
							me.multiselected.push(record);
							items.push(record);
							me.selModel.select(items);
						} else {
							me.selModel.deselect(record);
							Ext.Array.remove(me.multiselected, record);
						}
					}
				},
				onHeaderClick : function(headerCt, header, e) {
					if (header.isCheckerHd) {
						e.stopEvent();
						var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
						if (isChecked) {
							this.deselectAll(true);
							var grid = Ext.getCmp('excelgrid');
							this.deselect(grid.multiselected);
							grid.multiselected = new Array();
							var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
							Ext.each(els, function(el, index) {
								el.setAttribute('class', 'x-grid-row-checker');
							});
							header.el.removeCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');// 添加这个
						} else {
							var grid = Ext.getCmp('excelgrid');
							this.deselect(grid.multiselected);
							grid.multiselected = new Array();
							var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
							Ext.each(els, function(el, index) {
								el.setAttribute('class', 'x-grid-row-checker');
							});
							this.selectAll(true);
							header.el.addCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');// 添加这个
						}
					}
				}
			})
		});
	},
	getGridText : function(grid) {
		var texts = [];
		Ext.Array.each(grid.columns, function(c) {
			if (!c.hidden && (c.width > 0 || c.flex > 0) && !c.isCheckerHd) {
				var text = c.text || c.header;
				if (text) {
					if ((c.items && c.items.length > 0) || (c.columns && c.columns.length > 0)) {
						var items = (c.items && c.items.items) || c.columns;
						Ext.Array.each(items, function(i) {
							texts.push(text + '(' + (i.text || i.header) + ')');
						});
					} else{
						//2018040472 , 导入时，清除text存在的勾选框代码
						text = text.replace(/<.*?\/>/,'');						
						texts.push(text);
					}
				}
			}
		});
		return texts;
	},
	getGridHeaders : function(grid) {
		var cols = {};
		Ext.Array.each(grid.columns, function(c) {
			if (!c.hidden && (c.width > 0 || c.flex > 0) && !c.isCheckerHd) {
				var text = c.text || c.header;
				if (text) {
					if ((c.items && c.items.length > 0) || (c.columns && c.columns.length > 0)) {
						var items = (c.items && c.items.items) || c.columns;
						Ext.Array.each(items, function(i) {
							cols[text + '(' + (i.text || i.header) + ')'] = i;
						});
					} else
						cols[text] = c;
				}
			}
		});
		return cols;
	},
	/**
	 * excel没有grid列的dataIndex，只能根据grid列的header来匹配 From fGrid To tGrid
	 */
	exportGridToGrid : function(fGrid, tGrid, callback) {
		var me = this, cols = me.getGridHeaders(tGrid);
		var f = new Array();
		var rel = new Object();
		Ext.each(fGrid.columns, function(c) {
			if (!c.hidden && cols[c.text]) {
				f.push(c);
				rel[c.dataIndex] = cols[c.text];
			}
		});
		var fields = Ext.Array.pluck(f, 'dataIndex');
		var data = new Array();
		var o = null, errs = [];
		var r = fGrid.ownerCt.down('radiogroup'), m = r.getValue().import_mode;
		var detno = 1, len = tGrid.store.data.items.length;
		if (m == '+' && tGrid.detno) {
			detno = (tGrid.store.max(tGrid.detno) || 0) + 1;
		}
		Ext.each(fGrid.selModel.getSelection(), function(item, index) {
			o = new Object();
			if (m == '-') {
				if (index + 1 <= len) {
					o = tGrid.store.getAt(index).data; // 可以保留覆盖的原数据的其它值不变
					if(tGrid.detno && o[tGrid.detno]) detno++;
				}
			}
			var keys = Ext.Object.getKeys(item.data);
			Ext.each(keys, function(key) {
				if (Ext.Array.contains(fields, key)) {
					var toCol = rel[key], val = item.data[key];
					if(!me.isValid(val, toCol)) {
						errs.push('行' + (index + 1) + '(' + toCol.text + ')');
					} else {
						if(toCol.xtype == 'combocolumn' && toCol.editor)
							val = me.getComboValue(val, toCol.editor.store);
						else if(toCol.xtype == 'yncolumn')
							val = me.getYnValue(val);
						else if(toCol.xtype == 'datecolumn')
							val = me.getDateValue(val);
					}
					o[toCol.dataIndex] = val;
				}
			});
			if (tGrid.detno) {
				if (!o[tGrid.detno]) {
					o[tGrid.detno] = detno++;
				}
			}
			data.push(o);
		});
		if(errs.length > 0) {
			showError('数据未通过校验:<hr>' + errs.join('<br>'));
			return;
		}
		var mfields=(tGrid.necessaryFields || tGrid.necessaryField),detnofield=tGrid.detno;
		if (m == '+') {
			var start = tGrid.store.indexOf(tGrid.store.last());
			tGrid.store.add(data);
			tGrid.store.each(function(item, index) {
				if (index > start) {
					item.dirty = true;// 标记为已修改
					var m = {};
					if (mfields !== undefined) {
						if (mfields instanceof String) {
							m[mfields] = item.data[mfields];
						} else {
							Ext.each(mfields, function(f) {
								m[f] = item.data[f];
							});
						}
					}else m=Ext.clone(item.data);
					item.modified = m;
				}
			});
		} else if (m == '-') {
			tGrid.store.loadData(data);		
			Ext.each(tGrid.store.data.items, function(item,index) {
				var m = {};
				item.dirty = true;// 标记为已修改
				if (mfields !== undefined) {
					if (mfields instanceof String) {
						m[mfields] = item.data[mfields];
					} else {
						Ext.each(mfields, function(f) {
							m[f] = item.data[f];
						});
					}
				}else m=Ext.clone(item.data);
                item.modified=m;
			});
		}
		callback.call(me);
	},
	removeKeys : function(obj, keys) {
		var o = new Object();
		var key = Ext.Object.getKeys(obj);
		Ext.each(key, function(k) {
			if (!Ext.Array.contains(keys, k)) {
				o[k] = obj[k];
			}
		});
		return o;
	},
	isValid: function(v, toColumn) {
		if(v && toColumn) {
			var me = this;
			if(toColumn.xtype == 'numbercolumn')
				return me.isNumber(v);
			else if(toColumn.xtype == 'datecolumn')
				return me.isDate(v);
			else if(toColumn.xtype == 'combocolumn' && toColumn.editor)
				return me.isCombo(v, toColumn.editor.store);
			else if(toColumn.xtype == 'yncolumn')
				return me.isYn(v);
		}
		return true;
	},
	isNumber: function(v) {
		return !isNaN(parseFloat(v)) && isFinite(v);
	},
	isDate: function(v) {
		var dx1 = /\d{2,4}(-|\/)\d{1,2}(-|\/)\d{1,2}/,
		dx2 = /\d{1,2}(-|\/)\d{1,2}(-|\/)\d{4}/;
		return dx1.test(v) || dx2.test(v);
	},
	isCombo: function(v, store) {
		if(typeof store.each == 'undefined') {
			for(var i in store.data) {
				if(store.data[i].display == v || store.data[i].value == v)
					return true;
			}
		} else {
			var r;
			for(var i in store.data.items) {
				r = store.data.items[i];
				if(r.get('display') == v || r.get('value') == v)
					return true;
			}
		}
		return false;
	},
	isYn: function(v) {
		return [-1, '-1', 1, '1', 0, '0', '是', '否'].indexOf(v) > -1;
	},
	getComboValue: function(v, store) {
		var actual = v;
		if(typeof store.each == 'undefined') {
			Ext.Array.each(store.data, function(d){
				if(d.display == v)
					actual = d.value;
			});
		} else {
			store.each(function(r){
				if(r.get('display') == v)
					actual = r.get('value');
			});
		}
		return actual;
	},
	getYnValue: function(v) {
		return [-1, '-1', 1, '1', '是'].indexOf(v) > -1 ? -1 : 0;
	},
	getDateValue: function(v) {
		var dx1 = /\d{2,4}-\d{1,2}-\d{1,2}/,
		dx2 = /\d{2,4}\/\d{1,2}\/\d{1,2}/,
		dx3 = /\d{1,2}-\d{1,2}-\d{4}/;
		dx4 = /\d{1,2}\/\d{1,2}\/\d{4}/;
		if(dx1.test(v))
			return Ext.Date.parse(v, 'Y-m-d');
		else if(dx2.test(v))
			return Ext.Date.parse(v, 'Y/m/d');
		else if(dx3.test(v))
			return Ext.Date.parse(v, 'm-d-Y');
		else if(dx4.test(v))
			return Ext.Date.parse(v, 'm/d/Y');
		else
			return null;
	}
});