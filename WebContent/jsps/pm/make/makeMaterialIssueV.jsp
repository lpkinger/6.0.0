<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
 <link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ext/4.2/resources/ext-theme-gray/ext-theme-gray-all.css"/>
<%-- <link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link> --%>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<script type="text/javascript" src="<%=basePath %>resource/ext/4.2/ext-all-debug.js"></script> 
<%-- <script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script> --%>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/Export.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/RowExpander.js"></script>
<style>
	.x-panel .x-grid-body {
	    background: #f1f1f1;
	    border-color: #d0d0d0;
	    border-style: solid;
	    border-width: 1px;
	    border-top-color: #c5c5c5;
	}
	.x-grid-cell-inner {
	    height: 26px;
	    line-height: 26px;
	    overflow: hidden;
	    -o-text-overflow: ellipsis;
	    text-overflow: ellipsis;
	    padding: 0px 6px;
	    white-space: nowrap;
    }
	.custom-total .x-grid-cell{
		font-weight:bold;
		background-color: #CDB38B;
	}
	.x-form-display-field {
		font-size: 14px;
		color: blue
	}
	.x-grid-group-hd  {
	    padding: 6px;
	    background: #EEEEE0;
	    border-width: 0 0 2px 0;
	    border-style: solid;
	    border-color: #bcb1b0;
	    cursor: pointer;
	}
	.x-grid-highlight .x-grid-cell,.x-grid-highlight.x-grid-cell{
		background-color: #FFE4B5;
	}
	.x-panel-with-col-lines .x-grid-row .cell-split.x-grid-cell{
		border-right-style: dashed !important;
		background-color: #FFE4B5!important;
		border-right-color: #999 !important;
	}
</style>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.define('Ext.grid.plugin.CellEditing', {
    alias: 'plugin.cellediting',
    extend:  Ext.grid.plugin.Editing ,
                                                              
    lockableScope: 'both',

    
    
    
    

    init: function(grid) {
        var me = this,
            lockingPartner = me.lockingPartner;

        me.callParent(arguments);

        
        if (lockingPartner) {
            if (lockingPartner.editors) {
                me.editors = lockingPartner.editors;
            } else {
                me.editors = lockingPartner.editors = new Ext.util.MixedCollection(false, function(editor) {
                    return editor.editorId;
                });
            }
        } else {
            me.editors = new Ext.util.MixedCollection(false, function(editor) {
                return editor.editorId;
            });
        }
    },

    onReconfigure: function(grid, store, columns){
        
        if (columns) {
            this.editors.clear();
        }
        this.callParent();    
    },

    
    destroy: function() {
        var me = this;
        if (me.editors) {
            me.editors.each(Ext.destroy, Ext);
            me.editors.clear();
        }
        me.callParent(arguments);
    },

    onBodyScroll: function() {
    	//alert('scroll');
        var me = this,
            ed = me.getActiveEditor(),
            scroll = me.view.el.getScroll();

        
        
        if (ed && ed.editing && ed.editingPlugin === me) {
            
            if (scroll.top !== me.scroll.top) {
                if (ed.field) {
                    if (ed.field.triggerBlur) {
                        ed.field.triggerBlur();
                    } else {
                        ed.field.blur();
                    }
                }
            }
            
            else {
                ed.realign();
            }
        }
        me.scroll = scroll;
    },

    
    
    initCancelTriggers: function() {
        var me   = this,
            grid = me.grid,
            view = grid.view;
        me.mon(view, 'bodyscroll', me.onBodyScroll, me);
        me.mon(grid, {
            columnresize: me.cancelEdit,
            columnmove: me.cancelEdit,
            scope: me
        });
    },

    isCellEditable: function(record, columnHeader) {
        var me = this,
            context = me.getEditingContext(record, columnHeader);

        if (me.grid.view.isVisible(true) && context) {
            columnHeader = context.column;
            record = context.record;
            if (columnHeader && me.getEditor(record, columnHeader)) {
                return true;
            }
        }
    },

    
    startEdit: function(record, columnHeader,  context) {
        var me = this,
            ed;

        if (!context) {
            me.preventBeforeCheck = true;
            context = me.callParent(arguments);
            delete me.preventBeforeCheck;
            if (context === false) {
                return false;
            }
        }

        
        
        if (context && me.grid.view.isVisible(true)) {

            record = context.record;
            columnHeader = context.column;

            
            
            
            me.completeEdit();

            
            if (columnHeader && !columnHeader.getEditor(record)) {
                return false;
            }

            
            me.context = context;

            context.originalValue = context.value = record.get(columnHeader.dataIndex);
            
            if (me.beforeEdit(context) === false || me.fireEvent('beforeedit', me, context) === false || context.cancel) {
                return false;
            }

            ed = me.getEditor(record, columnHeader);

            
            me.grid.view.cancelFocus();
           // me.view.scrollCellIntoView(me.getCell(record, columnHeader));
            if (ed) {
                me.showEditor(ed, context, context.value);
                return true;
            }
            return false;
        }
    },

    showEditor: function(ed, context, value) {
        var me = this,
            record = context.record,
            columnHeader = context.column,
            sm = me.grid.getSelectionModel(),
            selection = sm.getCurrentPosition(),
            otherView = selection && selection.view;

        
        
       /* if (otherView && otherView !== me.view) {
            return me.lockingPartner.showEditor(ed, me.lockingPartner.getEditingContext(selection.record, selection.columnHeader), value);
        }*/

        me.setEditingContext(context);
        me.setActiveEditor(ed);
        me.setActiveRecord(record);
        me.setActiveColumn(columnHeader);

        
      /*  if (sm.selectByPosition && (!selection || selection.column !== context.colIdx || selection.row !== context.rowIdx)) {
            sm.selectByPosition({
                row: context.rowIdx,
                column: context.colIdx,
                view: me.view
            });
        }*/

        ed.startEdit(me.getCell(record, columnHeader), value, context);
        me.editing = true;
        me.scroll = me.view.el.getScroll();
    },

    completeEdit: function() {
        var activeEd = this.getActiveEditor();
        if (activeEd) {
            activeEd.completeEdit();
            this.editing = false;
        }
    },

    
    setEditingContext: function(context) {
        this.context = context;
        if (this.lockingPartner) {
            this.lockingPartner.context = context;
        }
    },

    setActiveEditor: function(ed) {
        this.activeEditor = ed;
        if (this.lockingPartner) {
            this.lockingPartner.activeEditor = ed;
        }
    },

    getActiveEditor: function() {
        return this.activeEditor;
    },

    setActiveColumn: function(column) {
        this.activeColumn = column;
        if (this.lockingPartner) {
            this.lockingPartner.activeColumn = column;
        }
    },

    getActiveColumn: function() {
        return this.activeColumn;
    },

    setActiveRecord: function(record) {
        this.activeRecord = record;
        if (this.lockingPartner) {
            this.lockingPartner.activeRecord = record;
        }
    },

    getActiveRecord: function() {
        return this.activeRecord;
    },

    getEditor: function(record, column) {
        var me = this,
            editors = me.editors,
            editorId = column.getItemId(),
            editor = editors.getByKey(editorId),
            
            editorOwner = me.grid.ownerLockable || me.grid;

        if (!editor) {
            editor = column.getEditor(record);
            if (!editor) {
                return false;
            }

            
            if (editor instanceof Ext.grid.CellEditor) {
                editor.floating = true;
            }
            
            else {
                editor = new Ext.grid.CellEditor({
                    floating: true,
                    editorId: editorId,
                    field: editor
                });
            }
            
            editorOwner.add(editor);
            editor.on({
                scope: me,
                specialkey: me.onSpecialKey,
                complete: me.onEditComplete,
                canceledit: me.cancelEdit
            });
            column.on('removed', me.cancelActiveEdit, me);
            editors.add(editor);
        }

        if (column.isTreeColumn) {
            editor.isForTree = column.isTreeColumn;
            editor.addCls(Ext.baseCSSPrefix + 'tree-cell-editor')
        }
        editor.grid = me.grid;
        
        
        editor.editingPlugin = me;
        return editor;
    },
    
    cancelActiveEdit: function(column){
        var context = this.context
        if (context && context.column === column) {
            this.cancelEdit();
        }   
    },
    
    
    setColumnField: function(column, field) {
        var ed = this.editors.getByKey(column.getItemId());
        Ext.destroy(ed, column.field);
        this.editors.removeAtKey(column.getItemId());
        this.callParent(arguments);
    },

    
    getCell: function(record, column) {
        return this.grid.getView().getCell(record, column);
    },

    onSpecialKey: function(ed, field, e) {
        var sm;
 
        if (e.getKey() === e.TAB) {
            e.stopEvent();

            if (ed) {
                
                
                ed.onEditorTab(e);
            }

            sm = ed.up('tablepanel').getSelectionModel();
            if (sm.onEditorTab) {
                return sm.onEditorTab(ed.editingPlugin, e);
            }
        }
    },

    onEditComplete : function(ed, value, startValue) {
        var me = this,
            activeColumn = me.getActiveColumn(),
            context = me.context,
            record;

        if (activeColumn) {
            record = context.record;

            me.setActiveEditor(null);
            me.setActiveColumn(null);
            me.setActiveRecord(null);
    
            context.value = value;
            if (!me.validateEdit()) {
                return;
            }

            
            
            if (!record.isEqual(value, startValue)) {
                record.set(activeColumn.dataIndex, value);
            }

          
            //context.view.focus(false, true);
            me.fireEvent('edit', me, context);
            me.editing = false;
        }
    },

    
    cancelEdit: function() {
        var me = this,
            activeEd = me.getActiveEditor();

        me.setActiveEditor(null);
        me.setActiveColumn(null);
        me.setActiveRecord(null);
        if (activeEd) {
            activeEd.cancelEdit();
            me.context.view.focus();
            me.callParent(arguments);
            return;
        }
        
        return true;
    },

    
    startEditByPosition: function(position) {

        
        if (!position.isCellContext) {
            position = new Ext.grid.CellContext(this.view).setPosition(position);
        }

        
        position.setColumn(this.view.getHeaderCt().getVisibleHeaderClosestToIndex(position.column).getIndex());

        return this.startEdit(position.record, position.columnHeader);
    }
});
Ext.override(Ext.Editor,{
	 completeEdit : function(remainVisible) {
	        var me = this,
	            field = me.field,
	            value;

	        if (!me.editing) {
	            return;
	        }

	        // Assert combo values first
	        if (field.assertValue) {
	            field.assertValue();
	        }

	        value = me.getValue();
	        if (!field.isValid()) {
	            if (me.revertInvalid !== false) {
	                me.cancelEdit(remainVisible);
	            }
	            return;
	        }

	        if (String(value) === String(me.startValue) && me.ignoreNoChange) {
	            me.hideEdit(remainVisible);
	            return;
	        }

	        if (me.fireEvent('beforecomplete', me, value, me.startValue) !== false) {
	            // Grab the value again, may have changed in beforecomplete
	            value = me.getValue();
	            if (me.updateEl && me.boundEl) {
	                me.boundEl.update(value);
	            }
	           me.hideEdit(remainVisible);
	           me.fireEvent('complete', me, value, me.startValue);
	        }
	    }

});
Ext.override(Ext.grid.feature.Grouping, {
    setup: function(rows, rowValues) {
        var me = this,
            data = me.refreshData,
            isGrouping = !me.disabled && me.view.store.isGrouped();
            //4.2 bug 修复锁列加载完之后重新赋值是否有groupFeatrue，来判断对应view,否则checkbox勾选出错
            this.view.isGrouping=isGrouping;
        
        me.skippedRows = 0;
        if (rowValues.view.bufferedRenderer) {
            rowValues.view.bufferedRenderer.variableRowHeight = true;
        }
        data.groupField = me.getGroupField();
        data.header = me.getGroupedHeader(data.groupField);
        data.doGrouping = isGrouping;
        rowValues.groupHeaderTpl = Ext.XTemplate.getTpl(me, 'groupHeaderTpl');
 
        if (isGrouping && me.showSummaryRow) {
            data.summaryData = me.generateSummaryData();
        }
    }
});
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'pm.make.MakeMaterialIssueV'
    ],
    launch: function() {
    	Ext.create('erp.view.pm.make.MakeMaterialIssueV');//创建视图
    }
});
var caller = 'MakeMaterial!issue';
var ifCanrepqty ;
var ifIncludingLoss;
var v_num=1;
</script>
</head>
<body >
</body>
</html>