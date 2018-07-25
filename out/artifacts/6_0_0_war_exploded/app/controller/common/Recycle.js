Ext.QuickTips.init();
Ext.define('erp.controller.common.Recycle', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil', 'erp.util.GridUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
    views: ['common.Recycle','core.form.Panel','core.form.Panel2','core.trigger.MultiDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger','core.form.SeparNumber',
             'core.form.ConDateField','core.form.YnField','core.form.CheckBoxContainer','core.form.FtFindField','core.form.MultiField',
             'core.grid.Panel2','core.grid.Panel', 'core.grid.Panel3', 'core.grid.Panel4', 'core.grid.Panel5',  'core.toolbar.Toolbar', 'core.form.MonthDateField','core.form.MonthDateField','core.form.CheckBoxGroup','core.grid.ItemGrid',
             'core.form.SpecialContainField','core.form.CheckBoxGroupEndwithS','core.grid.YnColumn',
             'core.button.GridWin', 'core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger', 'core.trigger.TextAreaTrigger',
      		 'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.CateTreeDbfindTrigger',
      		 ],
    init: function(){ 
    	var me = this;
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	me.GridUtil = Ext.create('erp.util.GridUtil');
    	me.FormUtil = Ext.create('erp.util.FormUtil');
    	me.RenderUtil = Ext.create('erp.util.RenderUtil');
    	this.control({ 
    		'panel[id=recycle_panel]': {
    			afterrender: function(){
    				me.getRecycles();
    			}
    		},
    		'#export': {
    			click: function() {
    				var p = Ext.getCmp('recycle_panel'), grid = p.down('grid');
    				if(grid)
    					me.BaseUtil.exportGrid(grid);
    			}
    		}
    	});
    },
    getRecycles: function(){
    	var me = this;
    	if(formCondition) {
    		var p = Ext.getCmp('recycle_panel');
    		var re_id = formCondition.replace(/IS/g, '=').split('=')[1];
    		p.setLoading(true);
    		Ext.Ajax.request({
    			url: basePath + 'common/recycle/getRecycles.action',
    			params: {
    				id: re_id
    			},
    			method: 'post',
    			callback: function(opt, s, r){
    				p.setLoading(false);
    				var res = Ext.decode(r.responseText);
    				if(res.success) {
    					var form = null,grid = null;
    					if(res.formset) {
    						form = me.createForm(res.formset, res.formdata);
    						if(res.gridset) {
    							grid = me.createGrid(res.gridset, res.griddata);
    						}
    					} else if(res.gridset) {
    						grid = me.createGrid(res.gridset, res.griddata);
    					}
    					if(form) {
    						if(!grid) {
    							form.anchor = '100% 100%';
    						}
    						p.add(form);
    					}
    					if(grid) {
    						if(!form) {
    							grid.anchor = '100% 100%';
    						}
    						p.add(grid);
    					}
    				}
    			}
    		});
    	}
    },
    createForm: function(set, data){
    	data = Ext.decode(data);
    	Ext.each(set.items, function(item){
    		if(screen.width < 1280){//根据屏幕宽度，调整列显示宽度
    			if(item.columnWidth > 0 && item.columnWidth <= 0.25){
    				item.columnWidth = 1/3;
    			} else if(item.columnWidth > 0.25 && item.columnWidth <= 0.5){
    				item.columnWidth = 2/3;
    			} else if(item.columnWidth >= 1){
    				item.columnWidth = 1;
    			}
    		} else {
    			if(item.columnWidth > 0.25 && item.columnWidth < 0.5){
    				item.columnWidth = 1/3;
    			} else if(item.columnWidth > 0.5 && item.columnWidth < 0.75){
    				item.columnWidth = 2/3;
    			}
    		}
    		item.fieldStyle = item.fieldStyle + ';background:#f1f1f1;';
			item.readOnly = true;
			if(item.name) {
				item.value = data[item.name] || data[item.name.toUpperCase()];
			}
			if(item.secondname){
				item.secondvalue = data[item.secondname.toUpperCase()];
			}
			if(item.xtype == 'checkbox' && item.value == 1){
				item.checked = true;
			}
    	});
    	return Ext.create('Ext.form.Panel', {
    		id: 'form',
    		anchor: '100% 50%',
    		frame : true,
    		title: set.title,
    		layout : 'column',
    		autoScroll : true,
    		defaultType : 'textfield',
    		labelSeparator : ':',
    		buttonAlign : 'center',
    		fieldDefaults : {
    		       margin : '2 2 2 2',
    		       fieldStyle : "background:#FFFAFA;color:#515151;",
    		       focusCls: 'x-form-field-cir',
    		       labelAlign : "right",
    		       msgTarget: 'side',
    		       blankText : $I18N.common.form.blankText
    		},
    		items: set.items,
    		FormUtil: Ext.create('erp.util.FormUtil')
    	});
    },
    createGrid: function(set, data){
    	var columns = set.gridColumns, fields = set.gridFields, datas = new Array(),dd,me = this;
    	Ext.each(data, function(d){
    		d = Ext.decode(d);dd = new Object();
    		Ext.each(fields, function(f){
    			dd[f.name] = d[f.name.toUpperCase()];
    		});
    		datas.push(dd);
    	});
    	Ext.each(columns, function(column, y){
    		if(!column.haveRendered && column.renderer != null && column.renderer != ""){
        		var renderName = column.renderer;
        		if(contains(column.renderer, ':', true)){
        			var args = new Array();
        			Ext.each(column.renderer.split(':'), function(a, index){
        				if(index == 0){
        					renderName = a;
        				} else {
        					args.push(a);
        				}
        			});
        			if(!me.RenderUtil.args[renderName]){
        				me.RenderUtil.args[renderName] = new Object();
        			}
        			me.RenderUtil.args[renderName][column.dataIndex] = args;
        		}
        		column.renderer = me.RenderUtil[renderName];
        		column.haveRendered = true;
        	}
    		var logic = column.logic;
    		if(logic != null){
    			if(logic == 'detno'){
    				column.width = 40;
    				column.renderer = function(val, meta) {
    			        meta.tdCls = Ext.baseCSSPrefix + 'grid-cell-special';
    			        return val;
    			    };
    			}
    		}
    	});
    	return Ext.create('Ext.grid.Panel', {
    		anchor: '100% 50%',
    		layout : 'fit',
    		id: 'grid', 
    	 	emptyText : $I18N.common.grid.emptyText,
    	    columnLines : true,
    	    autoScroll : true,
    	    RenderUtil: me.RenderUtil,
    	    store: Ext.create('Ext.data.Store', {
    	    	fields: fields,
    	    	data: datas
    	    }),
    	    columns: columns,
    	    bodyStyle:'background-color:#f1f1f1;'
    	});
    }
});
