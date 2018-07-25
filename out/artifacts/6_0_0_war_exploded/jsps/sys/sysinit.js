Ext.define("Ext.grid.column.Combo", {
	extend : "Ext.grid.column.Column",
	alias : ["widget.combocolumn"],
	constructor : function(a) {
		this.editor = arguments[0].editor;
		this.callParent(arguments);
		this.renderer = this.renderer || function(a, b, c, x, y, s, v){
			if(!Ext.isEmpty(a)) {					
				var g = v.ownerCt,h =b.column,f = h.field, k;
				if ((k = (h.editor || h.filter)) && k.store) {
					var t = null,dd = k.store.data;
					t = Ext.Array.filter(dd, function(d, index){
						return d.value == a;
					});
					if (t && t.length > 0) {
						return t[0].display;
					}
				} else if (f) {
					if(f.store) {
						var t = f.store.findRecord('value', a);
						if (t)
							return t.get('display');
					} else
						return f.rawValue;
				}
				return a;
			}
		};
	}
});
Ext.define('Ext.ux.CellDragDrop', {
	extend: 'Ext.AbstractPlugin',
	alias: 'plugin.celldragdrop',

	uses: ['Ext.view.DragZone'],

	/**
	 * @cfg {Boolean} enforceType
	 * Set to `true` to only allow drops of the same type.
	 *
	 * Defaults to `false`.
	 */
	enforceType: false,

	/**
	 * @cfg {Boolean} applyEmptyText
	 * If `true`, then use the value of {@link #emptyText} to replace the drag record's value after a node drop.
	 * Note that, if dropped on a cell of a different type, it will convert the default text according to its own conversion rules.
	 *
	 * Defaults to `false`.
	 */
	applyEmptyText: false,

	/**
	 * @cfg {Boolean} emptyText
	 * If {@link #applyEmptyText} is `true`, then this value as the drag record's value after a node drop.
	 *
	 * Defaults to an empty string.
	 */
	emptyText: '',

	/**
	 * @cfg {Boolean} dropBackgroundColor
	 * The default background color for when a drop is allowed.
	 *
	 * Defaults to green.
	 */
	dropBackgroundColor: 'green',

	/**
	 * @cfg {Boolean} noDropBackgroundColor
	 * The default background color for when a drop is not allowed.
	 *
	 * Defaults to red.
	 */
	noDropBackgroundColor: 'red',

	//<locale>
	/**
	 * @cfg {String} dragText
	 * The text to show while dragging.
	 *
	 * Two placeholders can be used in the text:
	 *
	 * - `{0}` The number of selected items.
	 * - `{1}` 's' when more than 1 items (only useful for English).
	 */
	dragText: '{0} selected row{1}',
	//</locale>

	/**
	 * @cfg {String} ddGroup
	 * A named drag drop group to which this object belongs. If a group is specified, then both the DragZones and
	 * DropZone used by this plugin will only interact with other drag drop objects in the same group.
	 */
	ddGroup: "GridDD",

	/**
	 * @cfg {Boolean} enableDrop
	 * Set to `false` to disallow the View from accepting drop gestures.
	 */
	enableDrop: true,

	/**
	 * @cfg {Boolean} enableDrag
	 * Set to `false` to disallow dragging items from the View.
	 */
	enableDrag: true,

	/**
	 * @cfg {Object/Boolean} containerScroll
	 * True to register this container with the Scrollmanager for auto scrolling during drag operations.
	 * A {@link Ext.dd.ScrollManager} configuration may also be passed.
	 */
	containerScroll: false,

	init: function (view) {
		var me = this;

		view.on('render', me.onViewRender, me, {
			single: true
		});
	},

	destroy: function () {
		var me = this;

		Ext.destroy(me.dragZone, me.dropZone);
	},

	enable: function () {
		var me = this;

		if (me.dragZone) {
			me.dragZone.unlock();
		}
		if (me.dropZone) {
			me.dropZone.unlock();
		}
		me.callParent();
	},

	disable: function () {
		var me = this;

		if (me.dragZone) {
			me.dragZone.lock();
		}
		if (me.dropZone) {
			me.dropZone.lock();
		}
		me.callParent();
	},

	onViewRender: function (view) {
		var me = this,
		scrollEl;

		if (me.enableDrag) {
			if (me.containerScroll) {
				scrollEl = view.getEl();
			}

			me.dragZone = new Ext.view.DragZone({
				view: view,
				ddGroup: me.dragGroup || me.ddGroup,
				dragText: me.dragText,
				containerScroll: me.containerScroll,
				scrollEl: scrollEl,
				getDragData: function (e) {
					var view = this.view,
					item = e.getTarget(view.getItemSelector()),
					record = view.getRecord(item),
					clickedEl = e.getTarget(view.getCellSelector()),
					dragEl;
					if (item) {
						dragEl = document.createElement('div');
						dragEl.className = 'x-form-text';
						dragEl.appendChild(document.createTextNode(clickedEl.textContent || clickedEl.innerText));
						//修改 单元格不能拖动
						if (view.getGridColumns()[clickedEl.cellIndex].enableDrag===false) return; 
						return {
							event: new Ext.EventObjectImpl(e),
							ddel: dragEl,
							item: e.target,
							columnName: view.getGridColumns()[clickedEl.cellIndex].dataIndex,
							record: record
						};
					}
				},

				onInitDrag: function (x, y) {
					var self = this,
					data = self.dragData,
					view = self.view,
					selectionModel = view.getSelectionModel(),
					record = data.record,
					el = data.ddel;
					if (!selectionModel.isSelected(record)) {
						selectionModel.select(record, true);
					}

					self.ddel.update(el.textContent || el.innerText);
					self.proxy.update(self.ddel.dom);
					self.onStartDrag(x, y);
					return true;
				}
			});
		}

		if (me.enableDrop) {        	
			me.dropZone = new Ext.dd.DropZone(view.el, {
				view: view,
				ddGroup: me.dropGroup || me.ddGroup,
				containerScroll: true,
				getTargetFromEvent: function (e) {
					var self = this,
					v = self.view,
					cell = e.getTarget(v.cellSelector),
					row, columnIndex;

					// Ascertain whether the mousemove is within a grid cell.
					if (cell) {
						row = v.findItemByChild(cell);
						columnIndex = cell.cellIndex;

						if (row && Ext.isDefined(columnIndex)) {
							//修改 单元格不能拖动
							if (self.view.up('grid').columns[columnIndex].enableDrop===false) return; 
							return {
								node: cell,
								record: v.getRecord(row),
								columnName: self.view.up('grid').columns[columnIndex].dataIndex
							};
						}
					}
				},

				// On Node enter, see if it is valid for us to drop the field on that type of column.
				onNodeEnter: function (target, dd, e, dragData) {
					var self = this,
					destType = target.record.fields.get(target.columnName).type.type.toUpperCase(),
					sourceType = dragData.record.fields.get(dragData.columnName).type.type.toUpperCase();

					delete self.dropOK;

					// Return if no target node or if over the same cell as the source of the drag.
					if (!target || target.node === dragData.item.parentNode) {
						return;
					}

					// Check whether the data type of the column being dropped on accepts the
					// dragged field type. If so, set dropOK flag, and highlight the target node.
					if (me.enforceType && destType !== sourceType) {

						self.dropOK = false;

						if (me.noDropCls) {
							Ext.fly(target.node).addCls(me.noDropCls);
						} else {
							Ext.fly(target.node).applyStyles({
								backgroundColor: me.noDropBackgroundColor
							});
						}

						return;
					}

					self.dropOK = true;

					if (me.dropCls) {
						Ext.fly(target.node).addCls(me.dropCls);
					} else {
						Ext.fly(target.node).applyStyles({
							backgroundColor: me.dropBackgroundColor
						});
					}
				},

				// Return the class name to add to the drag proxy. This provides a visual indication
				// of drop allowed or not allowed.
				onNodeOver: function (target, dd, e, dragData) {
					return this.dropOK ? this.dropAllowed : this.dropNotAllowed;
				},

				// Highlight the target node.
				onNodeOut: function (target, dd, e, dragData) {
					var cls = this.dropOK ? me.dropCls : me.noDropCls;

					if (cls) {
						Ext.fly(target.node).removeCls(cls);
					} else {
						Ext.fly(target.node).applyStyles({
							backgroundColor: ''
						});
					}
				},

				// Process the drop event if we have previously ascertained that a drop is OK.
				onNodeDrop: function (target, dd, e, dragData) {
					if (this.dropOK) {
						target.record.set(target.columnName, dragData.record.get(dragData.columnName));
						/**
						 * 同时覆盖其它信息
						 * */
						target.record.set('fromname',dragData.record.get('jo_name'));
						target.record.set('fromid',dragData.record.get('jo_id'));
						if (me.applyEmptyText) {
							dragData.record.set(dragData.columnName, me.emptyText);
						}
						return true;
					}
				},

				onCellDrop: Ext.emptyFn
			});
		}
	}
});

var required = '<span style="color:red;font-weight:bold" data-qtip="必填字段">*</span>';
var columnRequired=function(val){
	return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
	'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';
};
Ext.ns('SYSNIT');
SYSINIT={
		GRIDCOLUMNS:{
			SALE_CUSTOMERKIND:[{
				text:'类型编号',
				dataIndex:'ck_code'
			},{
				text:'类型名称',
				dataIndex:'ck_kind'
			},{
				text:'前缀码',
				dataIndex:'ck_excode'
			},{
				text:'当前数值',
				dataIndex:'ck_maxnum'
			},{
				text:'流水码长度',
				dataIndex:'ck_length'
			},{
				text:'备注信息',
				dataIndex:'ck_remark'
			},{
				text:'ID',
				dataIndex:'ck_id',
				width:0
			}],
			SALE_SALEKIND:[{
				text:'类型编号',
				dataIndex:'sk_code'
			},{
				text:'类型名称',
				dataIndex:'sk_name'
			},{
				text:'编号前缀',
				dataIndex:'sk_excode'
			},{
				text:'冲销触发类型',
				dataIndex:'sk_clashoption'	
			},{
				text:'冲销匹配规则',
				dataIndex:'sk_clashfor'
			},{
				text:'附加冲销条件',
				dataIndex:'sk_clashkind'
			},{
				text:'所属公司',
				dataIndex:'sk_cop'
			},{
				text:'取价原则',
				dataIndex:'sk_pricekind'
			},{
				dataIndex: 'sk_allowzero',
				text:'是否允许零单价'
			},{
				dataIndex:'sk_outtype',
				text:'出货类型'
			},{
				dataIndex:'sk_mrp',
				text:'是否参与MRP'
			},{
				dataIndex:'sk_salecatecode',
				text:'销售收入科目编号'
			},{
				dataIndex:'sk_salecatename',
				text:'sk_salecatename'
			},{
				dataIndex:'sk_costcatecode',
				text:'主营成本科目编号'
			},{
				dataIndex:'sk_costcatename',
				text:'主营成本科目名称'
			},{
				dataIndex:'sk_id',
				text:'ID'
			}],
			SALE_PAYMENTSSALE:[{
				dataIndex:'pa_code',
				text:'收款方式编号'
			},{
				dataIndex:'pa_beginby',
				text:'计算起始日'
			},{
				dataIndex:'pa_monthadd',
				text:'月增加'
			},{
				dataIndex:'pa_dayadd',
				text:'日增加'
			},{
				dataIndex:'pa_valid',
				text:'是否有效'
			},{
				text:'收款方式名称',
				dataIndex:'pa_name'
			},{
				text:'折扣率',
				dataIndex:'pa_discount'
			},{
				text:'币别',
				dataIndex:'pa_currency'
			},{
				text:'科目编号',
				dataIndex:'pa_catecode'
			},{
				text:'科目名称',
				dataIndex:'pa_catename'
			}],
			SALE_SALEFORECASTKIND:[{
				text:'类型编号',
				dataIndex:'sf_code'
			},{
				text:'类型名称',
				dataIndex:'sf_name'
			},{
				text:'编号字头',
				dataIndex:'sf_excode'
			},{
				text:'冲销类型',
				dataIndex:'sf_clashfor'
			},{
				text:'冲销选项',
				dataIndex:'sf_clashoption'
			},{
				text:'销售收入科目',
				dataIndex:'sf_salecatecode'
			},{
				text:'主营成本科目',
				dataIndex:'sf_costcatecode'
			},{
				text:'是否参与MRP',
				dataIndex:'sf_mrp'
			},{
				text:'ID',
				dataIndex:'sf_id'
			}],
			SALE_BORROWCARGOTYPE:[{
				text:'类型编号',
				dataIndex:'bt_code'
			},{
				text:'类型名称',
				dataIndex:'bt_name'			
			},{
				text:'默认科目',
				dataIndex:'bt_catecode'			
			},{
				text:'科目名称',
				dataIndex:'bt_catename'
			},{
				text:'是否参与MRP',
				dataIndex:'bt_ismrp'
			},{
				text:'ID',
				dataIndex:"bt_id'"
			}]

		}		
}

var required = '<span style="color:red;font-weight:bold" data-qtip="Required">*</span>';