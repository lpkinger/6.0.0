Ext.define('erp.view.common.EmpDbfind.EmpTreegrid',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.emptreegrid',
	id: 'emptreegrid',
	singleExpand: true,
	rootVisible: false, 
	containerScroll : true, 
	autoScroll: false, 
	useArrows: true,
	cls: 'custom',
	bodyStyle:'background-color:#f1f1f1;',
	initComponent : function(){ 
		this.getEmployeeTree();
		this.callParent(arguments);
	},
	getEmployeeTree: function(parentid, key, caller){
		var me = this;
		me.setLoading(true);
		Ext.Ajax.request({
        	url : basePath + 'hr/employee/tree.action',
        	timeout:60000,
        	callback : function(options, success, response){
        		if(success) {
        			var res = new Ext.decode(response.responseText);
            		if(res.success){
	        			me.getRootNode().appendChild(res.data);
            			me.setLoading(false);
            		} else if(res.exceptionInfo){
            			showError(res.exceptionInfo);
            		}
        		} else {
        			me.setLoading(false);
        		}
        	}
        });
	},
	store: Ext.create('Ext.data.TreeStore', {
		fields: [{
			name: 'em_id',
			type: 'int'
		},{
			name: 'em_defaultorid',
			type: 'int'
		},{
			name: 'em_defaulthsid',
			type: 'int'
		},'em_code','em_name','em_depart','em_defaultorname','em_position','em_mobile','em_uu'],
    	root : {
        	text: 'root',
    		expanded: true
    	}
	}),
	columns: [{
		text: '组织机构',
		xtype: 'treecolumn',
		flex: 1,
		dataIndex: 'em_defaultorname',
		renderer: function(val, meta, record) {
			var d = record.data.data || record.raw.data;
			return d.or_name;
		},
		filter: {
			xtype: 'textfield'
		}
	}, {
		text: '岗位名称',
		flex: 1.5,
		dataIndex: 'em_position',
		renderer: function(val, meta, record) {
			var d = record.data.data || record.raw.data;
			return d.em_position;
		},
		filter: {
			xtype: 'textfield'
		}
	}, {
		text: '员工编号',
		flex: 1,
		dataIndex: 'em_code',
		renderer: function(val, meta, record) {
			var d = record.data.data || record.raw.data;
			return d.em_code;
		},
		filter: {
			xtype: 'textfield'
		}
	}, {
		text: '员工姓名',
		flex: 1,
		dataIndex: 'em_name',
		renderer: function(val, meta, record) {
			var d = record.data.data || record.raw.data;
			return d.em_name;
		},
		filter: {
			xtype: 'textfield'
		}
	}, {
		text: '手机号',
		flex: 1.5,
		dataIndex: 'em_mobile',
		renderer: function(val, meta, record) {
			var d = record.data.data || record.raw.data;
			return d.em_mobile;
		},
		filter: {
			xtype: 'textfield'
		}
	}, {
		text: 'UU号',
		flex: 1,
		dataIndex: 'em_uu',
		renderer: function(val, meta, record) {
			var d = record.data.data || record.raw.data;
			return d.em_uu;
		},
		filter: {
			xtype: 'textfield'
		}
	}],
	listeners: {
		afterrender: function() {
			this.applyFilter();
		},
		checkchange: function(record, b) {
			
		},
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	},
	mixins: {
        treeFilter: 'erp.view.common.EmpDbfind.TreeFilter'
    },
	applyFilter: function() {
		var columns = this.view.headerCt.getGridColumns(true); 
		this.filterContainers = new Array();
        for(var c=0; c < columns.length; c++) {
            var column = columns[c];
            if(column.filter){
                var filterContainerConfig = {
                    id: column.id + '-filtersContainer',
                    cls: this.filterContainerCls,
                    layout: 'anchor',
                    bodyStyle: {'background-color': 'transparent', 'height': 'auto'},
                    border: false,
                    width: 500,
                    listeners: {
                        scope: this,
                        element: 'el',
                        mousedown: function(e){
                            e.stopPropagation();
                        },
                        click: function(e){
                            e.stopPropagation();
                        },
                        keydown: function(e){
                             e.stopPropagation();
                        },
                        keypress: function(e){
                             e.stopPropagation();
                             if(e.getKey() == Ext.EventObject.ENTER)
                             {
                                 this.onFilterContainerEnter();
                             }
                        },
                        keyup: function(e){
                             e.stopPropagation();
                        }
                    },
                    items: []
                };
                var fca = [].concat(column.filter);
                for(var ci = 0; ci < fca.length; ci++){
                    var fc = fca[ci];
                    Ext.applyIf(fc, {
                        filterName: column.dataIndex,
                        fieldLabel: column.text || column.header,
                        hideLabel: fca.length == 1
                    });
                    Ext.apply(fc, {
                        cls: this.filterFieldCls,
                        fieldStyle: 'background: #eee;',
                        itemId: fc.filterName,
                        focusCls: 'x-form-field-cir',
                        anchor: '-1'
                    });
                    var filterField = Ext.ComponentManager.create(fc);
                    filterField.column = column;
                    filterContainerConfig.items.push(filterField);
                }
                var filterContainer = Ext.create('Ext.container.Container', filterContainerConfig);
                filterContainer.render(column.el);
                column.setPadding = Ext.Function.createInterceptor(column.setPadding, function(h){return false;});
                this.filterContainers = Ext.Array.merge(this.filterContainers, filterContainer.items.items);
            }
        }
	},
	onFilterContainerEnter: function() {
		this.clearFilter();
		var me = this;
		Ext.each(this.filterContainers, function(f){
			if(!Ext.isEmpty(f.value)) {
				me.filterBy(f.value, f.filterName);
			}
		});
	}
});