/**
 * grid复制粘贴功能菜单
 * 
 * @author yingp
 */
Ext.define('erp.view.core.plugin.CopyPasteMenu', {
	ptype : 'copypastemenu',
	constructor : function(cfg) {
		if (cfg) {
			Ext.apply(this, cfg);
		}
	},
	clipPath : basePath + 'resource/ux/zero/ZeroClipboard.js',
	moviePath : basePath + 'resource/ux/zero/ZeroClipboard.swf',
	copyCls : 'grid-copy',
	init : function(grid) {
		this.grid = grid;
		this.execable = this.isExecable();
		if (grid.view) {
			var me = this;
			grid.on({
				cellclick : function(view, td, colIdx, record, tr, rowIdx, e) {
					if (e.ctrlKey) {
						me.loadSource();
						me.createMultiCopy(view, td, colIdx, record, tr, rowIdx, e);
						return false;
					}
					return true;
				},
				cellpaste : function(view, record, column, e) {
					e.preventDefault();
					Ext.defer(function(){
						me.onEditorPaste(view, record, column, e);
					}, 300);
				},
				specialkey : function(e) {
					console.log(e);
				}
			});
			if (grid.view.normalView) {
				grid.view.normalView.on('cellcontextmenu', me.onContextMenu, me);
				grid.view.lockedView.on('cellcontextmenu', me.onContextMenu, me);
			} else {
				grid.view.on('cellcontextmenu', me.onContextMenu, me);
			}
			Ext.defer(function(){
				me.loadSource();
			}, 2000);
		}
	},
	isExecable: function() {
		return Ext.isChrome && Number(Ext.userAgent.match(/chrome\/[\d.]+/gi)[0].replace(/[^0-9.]/ig,"").substring(0, 2)) > 42;
	},
	onContextMenu: function(view, td, colIdx, record, tr, rowIdx, e) {
		var me = this;
		me.loadSource();
		if (e.ctrlKey) {// 多行多列模式
			me.createMultiCopy(view, td, colIdx, record, tr, rowIdx, e);
		} else {
			me.createDefaultCopy(view, td, colIdx, record, tr, rowIdx, e);
		}
	},
	loadSource: function() {
		if (!this.execable && typeof ZeroClipboard === 'undefined') {
			var me = this;
			Ext.Loader.injectScriptElement(me.clipPath, function(){
				ZeroClipboard.moviePath = me.moviePath;
				var temp_ = Ext.DomHelper.createDom({
					style: 'display:none;'
				});
				var clip = new ZeroClipboard.Client();
				clip.glue(temp_);
			});
		}
	},
	createDefaultCopy : function(view, td, colIdx, record, tr, rowIdx, e) {
		var me = this, 
			column = view.getHeaderByCell(td) ||
				view.ownerCt.headerCt.getHeaderAtIndex(colIdx);
		if (!column) {
			return;
		}
		var dataIndex = column.dataIndex;
		e.preventDefault();
	    var menu = view.copymenu;
		if (!menu) {
			menu = view.copymenu = me.createMenu();
		}
		menu.showAt(e.getXY());
		me.clearCopyCls();
		menu.grid = view.ownerCt;
		menu.record = record;
		menu.column = column;
		menu.dataIndex = dataIndex;
		menu.cell = view.getCell(menu.record, menu.column);
		menu.cell.addCls(me.copyCls);
	},
	createMultiCopy : function(view, td, colIdx, record, tr, rowIdx, e) {
		var me = this, 
			column = view.getHeaderByCell(td) ||
				view.ownerCt.headerCt.getHeaderAtIndex(colIdx);
		if (!column) {
			return;
		}
		e.preventDefault();
	    var picker = view.copypicker;
		if (!picker) {
			picker = view.copypicker = me.createPicker(view);
		}
		picker.grid = view.ownerCt;
		picker.graphics = picker.graphics || new Array();
		var cell = view.getCell(record, column), 
			xy = {x : colIdx, y : rowIdx};
		if(Ext.Array.contains(picker.graphics, xy)) {
			picker.graphics.remove(xy);
		} else {
			picker.graphics.push(xy);
		}
		this.setCopyCls(view, picker.graphics);
		if(picker.graphics.length > 1) {
			picker.showAt([(cell.getX() + cell.getWidth()), (cell.getY() + cell.getHeight())]);
		}
	},
	setCopyCls : function(view, graphics) {
		var me = this;
		me.clearCopyCls();
		if (graphics.length == 1) {
			var cell = me.getCell(view, graphics[0].x, graphics[0].y);
			if (cell) {
				cell.addCls(me.copyCls);
			}
		} else if (graphics.length == 2) {
			var x = Math.min(graphics[0].x, graphics[1].x),
				m = Math.max(graphics[0].x, graphics[1].x),
				y = Math.min(graphics[0].y, graphics[1].y),
				n = Math.max(graphics[0].y, graphics[1].y);
			for (var i = x;i <= m;i++ ) {
				var cell = me.getCell(view, i, y);
				if (cell) {
					cell.addCls(me.copyCls + '-top');
				}
				cell = me.getCell(view, i, n);
				if (cell) {
					cell.addCls(me.copyCls + '-bottom');
				}
			}
			for (var i = y;i <= n;i++ ) {
				var cell = me.getCell(view, x, i);
				if (cell) {
					cell.addCls(me.copyCls + '-left');
				}
				cell = me.getCell(view, m, i);
				if (cell) {
					cell.addCls(me.copyCls + '-right');
				}
			}
		}
	},
	getCell : function(view, x, y) {
		var column = view.headerCt.getHeaderAtIndex(x),
			record = this.grid.store.getAt(y);
		if (column)
			return view.getCell(record, column);
		return null;
	},
	clearCopyCls : function() {
		var view = this.grid.view, me = this;
		var doms = view.getEl().query('.' + me.copyCls);
		if (doms) {
			Ext.each(doms, function() {
				me.removeClass(this, me.copyCls);
			});
		}
		if(view.copypicker) {
			var dir = ['top', 'bottom', 'left', 'right'];
			for(var i in dir) {
				var cls = me.copyCls + '-' + dir[i];
				doms = view.getEl().query('.' + cls);
				if (doms) {
					Ext.each(doms, function() {
						me.removeClass(this, cls);
					});
				}
			}
		}
	},
	removeClass : function(dom, className) {
		var temp = dom.className;
		dom.className = null;
		dom.className = temp.split(new RegExp(" " + className + "|" + className + " " + "|" + "^" + className + "$","ig")).join("");
	},
	createPicker : function(view) {
		var me = this;
		return Ext.create('Ext.menu.Menu', {
			view: view,
		    items : [{
		    	copyType : 'multi',
		    	iconCls : 'x-button-icon-copy',
		    	text : '复制',
		    	handler : function(item) {
		    		if(me.execable) {
						var m = item.ownerCt;
						me.onCopy(me.getMultiText(m.view, m.graphics));
					}
		    	}
		    },{
		    	text : '取消'
		    }],
		    listeners: {
				delay : 100,
				mouseover : function(m, item, e) {
					if (item && !me.execable) {
						me.resetClip(m, item);
					}
				},
				hide : function (m) {
					m.graphics = null;
					me.clearCopyCls();
				}
			}
		});
	},
	onCopy: function(text) {
		var target = Ext.DomHelper.createDom({
			tag: 'textarea',
			style: 'opacity: 0;position: absolute;top: -10000px;right: 0;',
			html: text
		}, document.body);
		target.focus();
		target.select();
	    document.execCommand('Copy');
	    target.blur();
	    document.body.removeChild(target);
	},
	createMenu : function() {
		var me = this;
		return Ext.create('Ext.menu.Menu', {
			bodyCls:'copyMenu',
			items: [{
				copyType : 'cell',
				iconCls : 'x-button-icon-copy',
				text : '复制单元格',
				handler: function(item) {
					if(me.execable) {
						var m = item.ownerCt;
						me.onCopy(me.getCellText(m.grid, m.record, m.column, m.dataIndex));
					}
				}
			},{
				copyType : 'row',
				text : '复制行',
				handler: function(item) {
					if(me.execable) {
						var m = item.ownerCt;
						me.onCopy(me.getRecordText(m.grid, m.record));
					}
				}
			},{
				copyType : 'table',
				text : '复制表格',
				handler: function(item) {
					if(me.execable) {
						var m = item.ownerCt;
						me.onCopy(me.getTableText(m.grid));
					}
				}
			},{
		    	xtype: 'menuseparator',cls:'x-copymenu-spt'
		    },{
				text : '复制到整列',
				handler : function(t, e) {
					var m = t.up('menu'),
						val = me.getCellText(m.grid, m.record, m.column, m.dataIndex);
		    		m && me.onColumnPaste(val, m.grid, m.column, m.record, m.dataIndex, m.cell, e);
		    	}
			},{
		    	xtype: 'menuseparator',cls:'x-copymenu-spt'
		    },{
		    	text : '粘贴',
		    	iconCls : 'x-button-icon-paste',
		    	handler : function() {
		    		me.onCellPaste();
		    	}
		    }],
			listeners: {
				delay : 100,
				mouseover : function(m, item, e) {
					if (item && !me.execable) {
						me.resetClip(m, item);
					}
				},
				hide : function (m) {
					me.clearCopyCls();
				}
			}
		});
	},
	resetClip : function(m, item) {
		if(!item.copyType) {
			return;
		}
		var me = this, clip = item.clip;
		if(!clip) {
			clip = item.clip = new ZeroClipboard.Client();
			clip.setHandCursor(true);
			clip.glue(item.id + '-itemEl', item.id);
			clip.addEventListener('complete', function (client, text) {
				m.hide();
			});
		}
		if(item.copyType == 'cell') {
			clip.setText(me.getCellText(m.grid, m.record, m.column, m.dataIndex));
		} else if(item.copyType == 'row') {
			clip.setText(me.getRecordText(m.grid, m.record));
		} else if(item.copyType == 'table') {
			clip.setText(me.getTableText(m.grid));
		} else if(item.copyType == 'multi') {
			clip.setText(me.getMultiText(m.view, m.graphics));
		}
	},
	getCellText : function(grid, record, column, dataIndex) {
		var v = record.get(dataIndex);
		if (v) {
			if(Ext.isDate(v)) {
				return Ext.Date.format(v, column.format || Ext.Date.defaultFormat);
			}
			return v;
		}
		return '';
	},
	getRecordText : function(grid, record) {
		var s = [], columns = grid.headerCt.getGridColumns(), v = null;
		Ext.each(columns, function(c){
			if(!c.hidden && c.dataIndex && c.getWidth() > 0) {
				v = record.get(c.dataIndex);
				if(c == null) {
					s.push(' ');
				} else {
					if(Ext.isDate(v)) {
						s.push(Ext.Date.format(v, c.format || Ext.Date.defaultFormat));
					} else {
						s.push(v);
					}
				}
			}
		});
		return s.join('\t');
	},
	getTableText : function(grid) {
		var me = this, s = [];
		grid.store.each(function(){
			s.push(me.getRecordText(grid, this));
		});
		return s.join('\n');
	},
	getMultiText : function(view, graphics) {
		if (graphics.length > 0) {
			var x = Math.min(graphics[0].x, graphics[1].x),
				m = Math.max(graphics[0].x, graphics[1].x),
				y = Math.min(graphics[0].y, graphics[1].y),
				n = Math.max(graphics[0].y, graphics[1].y);
			var me = this, s = [];
			for (var i = y;i <= n;i++ ) {
				var record = me.grid.store.getAt(i);
				var t = [];
				for (var j = x;j <= m;j++ ) {
					var	column = view.headerCt.getHeaderAtIndex(j);
					t.push(me.getCellText(me.grid, record, column, column.dataIndex));
				}
				s.push(t.join('\t'));
			}
			return s.join('\n');
		}
		return null;
	},
	onEditorPaste : function(view, record, column, e) {
		var me = this, v = e.target.value;
		if(v && (v.indexOf('\n') > -1 || v.indexOf('\t') > -1)) {
			var list = v.split('\n'), map, store = view.store,
				ct = view.ownerCt.headerCt,
				x = ct.getHeaderIndex(column),
				y = store.indexOf(record),
				nextRecord = record,
				nextColumn = column;
			Ext.Array.each(list, function(l, i){
				if(i > 0)
					nextRecord = store.getAt(y + i);
				if(nextRecord) {
					map = l.split('\t');
					Ext.Array.each(map, function(p, j){
						nextColumn = j > 1 ? me.getNextHeader(ct, nextColumn) : 
							(j == 1 ? me.getNextHeader(ct, column) : column);
						if(nextColumn) {
							nextRecord.set(nextColumn.dataIndex, p);
						}
						if(i == 0 && j == 0)
							e.target.value = p;
					});
				}
			});
		}
	},
	getNextHeader : function(headerCt, startColumn) {
		var me = this, index = headerCt.getHeaderIndex(startColumn), 
			next = headerCt.getHeaderAtIndex(index + 1);
		if(next) {
			if(!next.hidden && next.getWidth() > 0)
				return next;
			return me.getNextHeader(headerCt, next);
		}
		return null;
	},
	onCellPaste : function() {
		var v = this.getClipboard();
		if (!v) {
			return;
		}
		var menu = this.grid.view.copymenu, 
			record = menu.record, dataIndex = menu.dataIndex,
			column = menu.column;
		var editor = column.getEditor(record);
		if (!editor) {
			return;
		}
		if(column.xtype == 'datecolumn') {
			try {
				v = Ext.Date.parse(v, column.format || Ext.Date.defaultFormat);
			} catch (e) {
				alert('日期格式错误');return;
			}
		}
		record.set(dataIndex, v);
		if (editor.field) {
			editor.field.focus();
		}
	},
	onColumnPaste : function(val, grid, column, record, dataIndex, cell, e) {
		if(!grid.readOnly) {// 只允许可编辑的列粘贴
			var editor = column.getEditor(record);
			if(editor) {
				grid.store.each(function(record){
					record.set(dataIndex, val);
				});
			}
		}
	},
	getClipboard : function() {
		if (window.clipboardData) {
			return window.clipboardData.getData('Text');
		}
		return null;
	}
});