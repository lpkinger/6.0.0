Ext.define('Ext.ux.grid.GridHeaderFilters',{
    
    ptype: 'gridheaderfilters',
    
    alternateClassName: ['Ext.ux.grid.plugin.HeaderFilters', 'Ext.ux.grid.header.Filters'],
    
    requires: [
        'Ext.container.Container',
        'Ext.tip.ToolTip'
    ],

    grid: null,
    fields: null,
    containers: null,
    storeLoaded: false,
    filterFieldCls: 'x-gridheaderfilters-filter-field',
    filterContainerCls: 'x-gridheaderfilters-filter-container',
    filterRoot: 'data',
    tooltipTpl: '{[values.filters.length == 0 ? this.text.noFilter : "<b>"+this.text.activeFilters+"</b>"]}<br><tpl for="filters"><tpl if="value != \'\'">{[values.label ? values.label : values.property]} = {value}<br></tpl></tpl>',
    lastApplyFilters: null,
    
    text: {
        activeFilters: 'Active filters',
        noFilter: 'No filter'
    },
    
        stateful: true,
    
      reloadOnChange: true,
        
        ensureFilteredVisible: true,
        
       enableTooltip: true,
    
   constructor: function(cfg) 
   {  
       if(cfg)
       {
           Ext.apply(this, cfg);
       }
   },
    
   init: function(grid)
   {
       this.grid = grid;
                     if(!grid.headerFilters)
           grid.headerFilters = {};
        
       if(Ext.isBoolean(grid.statefulHeaderFilters))
       {
           this.setStateful(grid.statefulHeaderFilters);
       }
        
        this.grid.addEvents(
     
        'headerfilterchange',
                    'headerfiltersrender',
                    'beforeheaderfiltersapply',
                'headerfiltersapply'
        );
        
        this.grid.on({
            scope: this,
            reconfigure: this.renderFilters,
            columnresize: this.resizeFilterContainer,
            beforedestroy: this.onDestroy,
            beforestatesave: this.saveFilters,
            afterlayout: this.adjustFilterWidth
        });
        this.grid.getStore().on({
            scope: this,
            load: this.onStoreLoad
        });
        
        if(this.reloadOnChange)
        {
            this.grid.on('headerfilterchange',this.reloadStore, this);
        }
        
        if(this.stateful)
        {
            this.grid.addStateEvents('headerfilterchange');
        }
     
        Ext.apply(this.grid, 
        {
            headerFilterPlugin: this,
            setHeaderFilter: function(sName, sValue)
            {
                if(!this.headerFilterPlugin)
                    return;
                var fd = {};
                fd[sName] = sValue;
                this.headerFilterPlugin.setFilters(fd);
            },
                       getHeaderFilters: function()
            {
                if(!this.headerFilterPlugin)
                    return null;
                return this.headerFilterPlugin.getFilters();
            },
            
            setHeaderFilters: function(obj)
            {
                if(!this.headerFilterPlugin)
                    return;
                this.headerFilterPlugin.setFilters(obj);
            },
            getHeaderFilterField: function(fn)
            {
                if(!this.headerFilterPlugin)
                    return;
                if(this.headerFilterPlugin.fields[fn])
                    return this.headerFilterPlugin.fields[fn];
                else
                    return null;
            },
            resetHeaderFilters: function()
            {
                if(!this.headerFilterPlugin)
                    return;
                this.headerFilterPlugin.resetFilters();
            },
            clearHeaderFilters: function()
            {
                if(!this.headerFilterPlugin)
                    return;
                this.headerFilterPlugin.clearFilters();
            },
            applyHeaderFilters: function()
            {
                if(!this.headerFilterPlugin)
                    return;
                this.headerFilterPlugin.applyFilters();
            }
        });
   },
    
   
    
    saveFilters: function(grid, status)
    {
        status.savedHeaderFilters = this.stateful ? this.parseFilters() : grid.savedHeaderFilters;
    },
    
    setFieldValue: function(field, value)
    {
        var column = field.column;
        if(!Ext.isEmpty(value))
        {
            field.setValue(value);
            if(!Ext.isEmpty(value) && column.hideable && !column.isVisible() && !field.isDisabled() && this.ensureFilteredVisible)
            {
                column.setVisible(true);
            }
        }
        else
        {
            field.setValue('');
        }
    },
    renderFilters: function() {
    	var me = this;
        this.destroyFilters();
        this.fields = {};
        this.containers = {};
        var filters = this.grid.headerFilters;
        
                if(this.stateful && this.grid.savedHeaderFilters && !this.grid.ignoreSavedHeaderFilters)
        {
            Ext.apply(filters, this.grid.savedHeaderFilters);
        }
        this.grid.body.dom.style.top = '46px';//
        var storeFilters = this.parseStoreFilters();
        filters = Ext.apply(storeFilters, filters);
        
        var columns = this.grid.headerCt.getGridColumns(true);
        for(var c=0; c < columns.length; c++)
        {
            var column = columns[c];
            if(column.filter)
            {
                var filterContainerConfig = {
                    id: column.id + '-filtersContainer',
                    cls: this.filterContainerCls,
                    layout: 'anchor',
                    bodyStyle: {'background-color': 'transparent'},
                    border: false,
                    width: column.getWidth(),
                    listeners: {
                        scope: this,
                        element: 'el',
                        mousedown: function(e)
                        {
                            e.stopPropagation();
                        },
                        click: function(e)
                        {
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
                    
                for(var ci = 0; ci < fca.length; ci++)
                {
                    var fc = fca[ci];
                    if ((fc.xtype == 'combo' || fc.xtype == 'combofield') && fc.store) {
                    	Ext.Array.insert(fc.store.data, 0, [{
							display: '-所有-',value: '-所有-'
                    	},{
                    		display: '-无-',value: '-无-'
                    	}]);
                    }
                    Ext.applyIf(fc, {
                        filterName: column.dataIndex,
                        fieldLabel: column.text || column.header,
                        hideLabel: fca.length == 1
                    });
                    if(fc.xtype == 'datefield')
                    	fc.beforeBlur = Ext.emptyFn;
                    var initValue = Ext.isEmpty(filters[fc.filterName]) ? null : filters[fc.filterName];
                    Ext.apply(fc, {
                        cls: this.filterFieldCls,
                        fieldStyle: 'background: #eee;',
                        itemId: fc.filterName,
                        focusCls: 'x-form-field-cir',
                        anchor: '-1'
                    });
                    var filterField = Ext.ComponentManager.create(fc);
                    filterField.column = column;
                    this.setFieldValue(filterField, initValue);
                    this.fields[filterField.filterName] = filterField;
                    filterContainerConfig.items.push(filterField);
                    if (fc.xtype == 'combo' || fc.xtype == 'combofield') {
	                    filterField.on('change', function(field,newValue){
	                    	me.onFilterContainerEnter();// apply when combo change // add by yingp
	                    	field.select(newValue);
	                    });
                    }
                }
                
                var filterContainer = Ext.create('Ext.container.Container', filterContainerConfig);
                filterContainer.render(column.el);
                this.containers[column.id] = filterContainer;
                column.setPadding = Ext.Function.createInterceptor(column.setPadding, function(h){return false;});
            }
        }
        
        if(this.enableTooltip)
        {
            this.tooltipTpl = new Ext.XTemplate(this.tooltipTpl,{text: this.text});
            this.tooltip = Ext.create('Ext.tip.ToolTip',{
                target: this.grid.headerCt.el,
                //delegate: '.'+this.filterContainerCls,
                renderTo: Ext.getBody()
            });
            this.tooltip.setDisabled(true);
            this.grid.on('headerfilterchange',function(grid, filters)
            {
                var sf = filters.filterBy(function(filt){
                    return !Ext.isEmpty(filt.value);
                });
                if(sf.length>0&&this.tooltip.disabled){
                	this.tooltip.setDisabled(false);
                }
                this.tooltip.update(this.tooltipTpl.apply({filters: sf.getRange()}));
            },this);
        }
        
        this.applyFilters();
        this.grid.fireEvent('headerfiltersrender',this.grid,this.fields,this.parseFilters());
    },
    
    onStoreLoad: function()
    {
        this.storeLoaded = true;
    },
    
    onFilterContainerEnter: function()
    {
        this.applyFilters();
    },
    
    resizeFilterContainer: function(headerCt,column,w,opts)
    {
         if(!this.containers)             return;
        var cnt = this.containers[column.id];
        if(cnt)
        {
            cnt.setWidth(w);
            cnt.doLayout();
        }
    },
    
    destroyFilters: function()
    {
        if(this.fields)
        {
            for(var f in this.fields)
                Ext.destroy(this.fields[f]);
            delete this.fields;
        }
    
        if(this.containers)
        {
            for(var c in this.containers)
                Ext.destroy(this.containers[c]);
            delete this.containers;
        }
    },
    
    onDestroy: function()
    {
        this.destroyFilters();
        Ext.destroy(this.tooltip, this.tooltipTpl);
    },
    
     adjustFilterWidth: function() 
    {   
    	if(!this.containers){
    		return ;
    	}
        var columns = this.grid.headerCt.getGridColumns(true);        
        for(var c=0; c < columns.length; c++) 
        {           
            var column = columns[c];            
            if (column.filter && column.flex) 
            {               
                this.containers[column.id].setWidth(column.getWidth()-1);            
            }
          }
     },
   
    resetFilters: function()
    {
        if(!this.fields)
            return;
        for(var fn in this.fields)
        {
            var f = this.fields[fn];
            if(!f.isDisabled() && !f.readOnly && Ext.isFunction(f.reset))
                f.reset();
        }
        this.applyFilters();
    },
    
    clearFilters: function()
    {
        if(!this.fields)
            return;
        for(var fn in this.fields)
        {
            var f = this.fields[fn];
            if(!f.isDisabled() && !f.readOnly)
                f.setValue('');
        }
        this.applyFilters();
    },
    
    setFilters: function(filters)
    {
        if(!filters)
            return;
        
        if(Ext.isArray(filters))
        {
            var conv = {};
            Ext.each(filters, function(filter){
                if(filter.property)
                {
                    conv[filter.property] = filter.value; 
                }
            });
            filters = conv;
        }
        else if(!Ext.isObject(filters))
        {
            return;
        }




        this.initFilterFields(filters);
        this.applyFilters();
    },
    
    getFilters: function()
    {
        var filters = this.parseFilters();
        var res = new Ext.util.MixedCollection();
        for(var fn in filters)
        {
            var value = filters[fn];
            var field = this.fields[fn];
            res.add(new Ext.util.Filter({
                property: fn,
                value: value,
                root: this.filterRoot,
                label: field.fieldLabel
            }));
        }
        return res;
    },
    
    parseFilters: function()
    {
        var filters = {};
        if(!this.fields)
            return filters;
        for(var fn in this.fields)
        {
            var field = this.fields[fn];
            if(!field.isDisabled())
                filters[field.filterName] = field.getValue();
        }
        return filters;
    },
    
    initFilterFields: function(filters)
    {
        if(!this.fields)
            return;




        for(var fn in  filters)
        {
            var value = filters[fn];
            var field = this.fields[fn];
            if(field)
            {
                this.setFieldValue(filterField, initValue);
            }
        }
    },
    
    countActiveFilters: function()
    {
        var fv = this.parseFilters();
        var af = 0;
        for(var fn in fv)
        {
            if(!Ext.isEmpty(fv[fn]))
                af ++;
        }
        return af;
    },
    
    parseStoreFilters: function()
    {
        var sf = this.grid.getStore().filters;
        var res = {};
        sf.each(function(filter){
            var name = filter.property;
            var value = filter.value;
            if(name && value)
            {
                res[name] = value;            
            }
        });
        return res;
    },
    
    applyFilters: function()
    {
        var filters = this.parseFilters();
        if(this.grid.fireEvent('beforeheaderfiltersapply', this.grid, filters, this.grid.getStore()) !== false)
        {
            var storeFilters = this.grid.getStore().filters, filterArr = new Array();
            var exFilters = storeFilters.clone();
            var change = false;
            var active = 0;
            for(var fn in filters)
            {
                var value = filters[fn];
                
                var sf = storeFilters.findBy(function(filter){
                    return filter.property == fn;
                });
                
                if(Ext.isEmpty(value))
                {
                    if(sf)
                    {
                        storeFilters.remove(sf);
                        change = true;
                    }
                }
                else
                {
                    var field = this.fields[fn];
                    if(!sf || sf.value != filters[fn])
                    {
                        filterArr.push({
                        	root: this.filterRoot,
                        	label: field.fieldLabel,
                        	property: fn,
                            value: filters[fn]
                        });
                        if(sf)
                        {
                            storeFilters.remove(sf);
                        }
                        change = true;
                    }
                    active ++;
                }
            }
            
            this.grid.fireEvent('headerfiltersapply', this.grid, filters, active, this.grid.getStore());
            if(change || storeFilters.length != filterArr.length)
            {// update by yingp // filter bug
            	var filter = new Ext.util.Filter({
            		property: '$all',
            		filterArr: filterArr,
                    filterFn: function(item) {
                    	var args = arguments.callee.caller.caller.caller.arguments[0],
                    		r = args[0].filterArr, d = item.data;
                    	for(j in r) {
                    		var n = r[j].property, v = r[j].value;
                    		if(!Ext.isEmpty(d[n])) {
                        		if (String(d[n]).indexOf(v) == -1)
                        			return false;
                        	} else {
                        		return false;
                        	}
                    	}
                    	return true;
                    }
                });
                var ff = this.grid.getStore().filters.findBy(function(filter){
                    return filter.property == '$all';
                });
                if(ff) {
                	this.grid.getStore().filters.remove(ff);
                }
                this.grid.getStore().filters.add(filter);
                var curFilters = this.getFilters();
                this.grid.fireEvent('headerfilterchange', this.grid, curFilters, this.lastApplyFilters, active, this.grid.getStore());
                this.lastApplyFilters = curFilters;
            }
        }
    },
    
    reloadStore: function()
    {
        var gs = this.grid.getStore();
        if(this.grid.getStore().remoteFilter)
        {
            if(this.storeLoaded)
            {
                gs.currentPage = 1;
                gs.load();
            }
        }
        else
      {
            if(gs.filters.getCount()) 
         {
                if(!gs.snapshot)
                    gs.snapshot = gs.data.clone();
                else
                {
                    gs.currentPage = 1;
                }
            gs.data = gs.snapshot.filter(gs.filters.getRange());
            var doLocalSort = gs.sortOnFilter && !gs.remoteSort;
            if(doLocalSort) 
                {
                    gs.sort();
                }
            // fire datachanged event if it hasn't already been fired by doSort
            if (!doLocalSort || gs.sorters.length < 1) 
            {
                gs.fireEvent('datachanged', gs);
                }
            }
           else
           {
                if(gs.snapshot)
                {
                    gs.currentPage = 1;
                    gs.data = gs.snapshot.clone();
                 delete gs.snapshot;
                 gs.fireEvent('datachanged', gs);
                }
            }
        }
    }
});

