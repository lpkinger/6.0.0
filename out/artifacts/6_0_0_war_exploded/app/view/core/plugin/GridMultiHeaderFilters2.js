	Ext.define('erp.view.core.plugin.GridMultiHeaderFilters2',{
    
    ptype: 'gridheaderfilters',
    
    alternateClassName: ['Ext.ux.grid.plugin.HeaderFilters', 'Ext.ux.grid.header.Filters'],
    
    requires: [
        'Ext.container.Container',
        'Ext.tip.ToolTip'
    ],
    pluginId:'gridheaderfilters',
    grid: null,
    fields: null,
    containers: null,
    storeLoaded: false,
    filterFieldCls: 'x-gridheaderfilters-filter-field',
    filterContainerCls: 'x-gridheaderfilters-filter-container',
    filterRoot: 'data',
    tooltipTpl: '{[values.filters.length == 0 ? this.text.noFilter : "<b>"+this.text.activeFilters+"</b>"]}<br><tpl for="filters"><tpl if="value != \'\'">{[values.label ? values.label : values.property]}<tpl if="value != \'\'">:<tpl if="type != \'\'">{type}</tpl>{value}<br></tpl></tpl>',
    lastApplyFilters: null,
    textfieldArr:['direct','nodirect','head','end','vague','novague','null'],
    numberfieldArr:['=','!=','>','<','>=','<=','~'],
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
                    	Ext.applyIf(fc, {
                            id: column.dataIndex+'Filter',
                            emptyText:column.filterJson_.value||'',
                            originalxtype:fc.xtype,
                            filterType:'='
                        });
                    }
                    Ext.applyIf(fc, {
                        filterName: column.dataIndex,
                        fieldLabel: column.text || column.header,
                        hideLabel: fca.length == 1,
                        name_:column.name_,
                        filterJson_:column.filterJson_,
                        emptyText:"",
                        originalxtype:fc.xtype,
                        id:column.dataIndex+'Filter'
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
                    me.parseFC(column,fc);
                    var filterField = Ext.ComponentManager.create(fc);
                    filterField.column = column;
                    this.setFieldValue(filterField, initValue);
                    this.fields[filterField.filterName] = filterField;
                    filterContainerConfig.items.push(filterField);
                    if (fc.xtype == 'combo' || fc.xtype == 'combofield') {
                    	filterField.enableKeyEvents = true;
                    	if(column.width<26){
                    		filterField.hideTrigger = true;
                    	}
	                    filterField.on('change', function(field,newValue){
	                    	if(!field.isChange){
		                    	me.onFilterContainerEnter();// apply when combo change // add by yingp
		                    	field.select(newValue);
	                    	}else{
	                    		field.isChange = false;
	                    	}
	                    });
	                    column.filterField = filterField;
	                    column.on('resize', function(field,newValue){
	                    	var t = field.filterField;
	                    	if((t.xtype == 'combo' || t.xtype == 'combofield')&&t.hideTrigger==true){
	                			var width = parseInt(t.getWidth());
	                			t.hideTrigger = false;
	                			t.inputEl.dom.style.width = (Number(width) - 18)+'px';
	                			t.triggerEl.item(0).dom.parentNode.style.width = '17px';
	                			t.triggerEl.item(0).dom.parentNode.style.display = 'block';
	                			t.triggerEl.item(0).setDisplayed('block');
	                			t.triggerEl.item(0).setWidth(17);
	                		}
	                    });
                    }
                }
                
                var filterContainer = Ext.create('Ext.container.Container', filterContainerConfig);
                if(filterField.originalxtype=='numberfield'||filterField.originalxtype=='datefield'){
                	filterField.maskRe =new RegExp('[0123456789\.\-\]+|(\\s)'); 
                }
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
            this.grid.on('tooltipchange',function(grid, filters)
            {
            	if(filters){
	                var sf = filters.filterBy(function(filt){
	                	if(filt.originalxtype=='combo'){
	                		filt.value = filt.comboValue;
	                	}
	                    return !Ext.isEmpty(filt.value);
	                });
	                if(sf.length!=0){
	                	this.tooltip.update(this.tooltipTpl.apply({filters: sf.getRange()}));
	                }else{
	                	this.tooltip.update('当前无筛选条件');
	                }
            	}else{
            		this.tooltip.update('当前无筛选条件');
            	}
            },this);
            this.grid.on('headerfilterchange',function(grid, filters)
            {
            	if(filters){
	                var sf = filters.filterBy(function(filt){
	                	if(filt.originalxtype=='combo'){
	                		filt.value = filt.comboValue;
	                	}
	                    return !Ext.isEmpty(filt.value);
	                });
	                if(sf.length!=0){
	                	this.tooltip.update(this.tooltipTpl.apply({filters: sf.getRange()}));
	                }else{
	                	this.tooltip.update('当前无筛选条件');
	                }
            	}else{
            		this.tooltip.update('当前无筛选条件');
            	}
            },this);
        }
        this.applyFilters(true);
        var id_ = columns[1].id_;
        //判断是否有默认筛选方案
        if(id_!=''&&id_!='null'&&id_!=null){
        	me.addEmptyText();
        	this.grid.fireEvent('headerfilterchange',this.grid,this.getFilters(true));
        }else{
        	this.tooltip.update('当前无筛选条件');
        }
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
    
    getFilters: function(isDefault)
    {
        var filters = isDefault?this.parseFiltersDefault():this.parseFilters();
        var res = new Ext.util.MixedCollection();
        var numplugin = Ext.getCmp('newrownumberer');
        for(var fn in filters)
        {
            var value = filters[fn];
            var field = this.fields[fn];
            res.add(new Ext.util.Filter({
                property: fn,
                value: value,
                root: this.filterRoot,
                label: field.fieldLabel,
                type:numplugin&&field.originalxtype=='textfield'?numplugin.textField[field.filterType]:'',
                originalxtype:field.originalxtype,
                comboValue:field.originalxtype=='combo'?field.getRawValue():''
            }));
        }
        return res;
    },
    
   parseFilters: function(isFirst)
    {
        var filters = {};
        if(!this.fields)
            return filters;
        for(var fn in this.fields)
        {
            var field = this.fields[fn];
            if(!field.isDisabled()){
            	if(isFirst){
            		filters[field.filterName] = field.getValue();
            	}else{
	            	var newValue = field.rawValue==field.emptyText?field.rawValue:field.getValue();
	            	if(newValue==''){
	            		var type = field.filterJson_.type;
	            		var defaultvalue = field.emptyText;
	            		if((field.originalxtype == 'numberfield' || field.originalxtype == 'datefield')&&defaultvalue&&defaultvalue.indexOf('~')==-1&&defaultvalue.indexOf('>')==-1&&defaultvalue.indexOf('<')==-1&&defaultvalue.indexOf('=')==-1&&defaultvalue.indexOf('>=')==-1&&defaultvalue.indexOf('<=')==-1&&defaultvalue.indexOf('!=')==-1){
	                		filters[field.filterName] = type+defaultvalue||'';
	                	}else{
	                		filters[field.filterName] = defaultvalue||'';
	                	}
	            		if(field.originalxtype == 'combo'){
	            			field.filterType = '=';
	            		}else if(!field.fromQuery){
	            			field.filterType = type||'';
	            		}
	            	}else{
	            		value = newValue;
                    	if(field.originalxtype == 'numberfield') {
                			if(value.indexOf('>=')==0||value.indexOf('<=')==0||value.indexOf('>')==0||value.indexOf('<')==0||value.indexOf('!=')==0||value.indexOf('=')==0){
                				var reg = /^(>=|>|<=|<|=|!=)?(-)?([1-9][0-9]+|[0-9])([.][0-9]+)?$/;
                				if(value.match(reg)==null){
                					showError('输入筛选数据有误,请重新输入!');
                					return false;
                				}
                			}else if(value.indexOf('~')>-1){
                				var arr = value.split('~');
                				var reg = /^(-)?([1-9][0-9]+|[0-9])([.][0-9]+)?~(-)?([1-9][0-9]+|[0-9])([.][0-9]+)?$/;
                				if(value.match(reg)==null){
                					showError('输入筛选数据有误,请重新输入!');
                					return false;
                				}else if(Number(arr[0])>Number(arr[1])){
                					showError('前面的数字不能大于后面的数字!');
                					return false;
                				}
                			}else if(value.indexOf('>=')>-1||value.indexOf('<=')>-1||value.indexOf('>')>-1||value.indexOf('<')>-1||value.indexOf('!=')>-1||value.indexOf('=')>-1){
            					showError('输入筛选数据有误,请重新输入!');
            					return false;
            				}else{
            					var reg = /^(-)?([1-9][0-9]+|[0-9])([.][0-9]+)?$/;
                				if(value.match(reg)==null){
                					showError('输入筛选数据有误,请重新输入!');
                					return false;
                				}
            				}
                    	} else if(field.originalxtype == 'datefield'){
                    			if(value.indexOf('=')>-1){
                    				var reg = /^(>=||<=|=)?/;
                    				if(value.match(reg)==null){
                    					showError('输入筛选数据有误,请重新输入!');
                    					return false;
                    				}
                    				var valueX = value.split('=')[1];
                    				var length = valueX.split('-').length;
                    				if(length<3){
                    					if(length == 1){
                    						var reg = /^=[0-9]{4}$/;
                            				if(value.match(reg)==null){
                            					showError('输入筛选数据有误,请重新输入!');
                            					return false;
                            				}
                    					}else if(length == 2){
                    						var reg = /^=[0-9]{4}-((0[1-9])|(1[0-2])){1}$/;
                            				if(value.match(reg)==null){
                            					showError('输入筛选数据有误,请重新输入!');
                            					return false;
                            				}
                    					}
	                    			}else {
	                    				var reg = /^(>=|<=|=)?[0-9]{4}-((0[1-9])|(1[0-2])){1}-((0[1-9])|((1|2)[0-9])|((3)[0-1])){1}$/;
	                    				if(value.match(reg)==null){
                        					showError('输入筛选数据有误,请重新输入!');
                        					return false;
                        				}
	                    			}
                    			}else if(value.indexOf('~')>-1){
                    				var reg = /^[0-9]{4}-((0[1-9])|(1[0-2])){1}-((0[1-9])|((1|2)[0-9])|((3)[0-1])){1}~[0-9]{4}-((0[1-9])|(1[0-2])){1}-((0[1-9])|((1|2)[0-9])|((3)[0-1])){1}$/;
                    				if(value.match(reg)==null){
                    					showError('输入筛选数据有误,请重新输入!');
                    					return false;
                    				}
                    			}else{
                    				var reg = /^[0-9]{4}-((0[1-9])|(1[0-2])){1}-((0[1-9])|((1|2)[0-9])|((3)[0-1])){1}$/;
                    				if(value.match(reg)==null){
                    					showError('输入筛选数据有误,请重新输入!');
                    					return false;
                    				}
                    			}
	                        }
	            		filters[field.filterName] = newValue;
	            	}
            	}
            }
        }
        return filters;
    },

    //第一次加载时的filter依据默认筛选方案
    parseFiltersDefault: function()
    {
        var filters = {};
        if(!this.fields)
            return filters;
        for(var fn in this.fields)
        {
            var field = this.fields[fn];
            if(!field.isDisabled()){
            	if((field.originalxtype == 'numberfield' || field.originalxtype == 'datefield')&&field.filterJson_.value&&field.filterJson_.value.indexOf('~')==-1){
            		filters[field.filterName] = field.filterJson_.type+field.filterJson_.value||'';
            	}else{
            		filters[field.filterName] = field.filterJson_.value||'';
            	}
            	if(field.originalxtype == 'combo'){
        			field.filterType = '=';
        		}else{
        			field.filterType = field.filterJson_.type||'';
        		}
               
            }
        }
        return filters;
    },
    //获取默认筛选方案中的字段
    addEmptyText:function(){
    	var filters = this.parseFiltersDefault();
    	var columns = this.grid.columns;
        var fontArr = [];
        for(var fn in filters)
        {
            var value = filters[fn];
            if(!Ext.isEmpty(value)){
            	fontArr.push(fn);
            }
        }
        this.changeFont(fontArr,columns);
       
    },
    //修改column的field的字体样式
    changeFont:function(arr,columns){
    	 for(var i in columns){
     		if(arr.indexOf(columns[i].dataIndex)>-1){
     			columns[i].textEl.dom.style.fontStyle='italic';
     			columns[i].textEl.dom.style.fontWeight='bold';
     			columns[i].textEl.dom.style.fontSize='13px';
     			//columns[i].textEl.dom.style.textDecoration='underline';
     		}else{
     			columns[i].textEl.dom.style.fontStyle='';
     			columns[i].textEl.dom.style.fontWeight='';
     			columns[i].textEl.dom.style.fontSize='14px';
     			//columns[i].textEl.dom.style.textDecoration='';
     		}
     	}
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
    
   applyFilters: function(isFirst)
    {
        var filters = isFirst?this.parseFilters(true):this.parseFilters();
        if(!filters){
        	return;
        }
        if(this.grid.fireEvent('beforeheaderfiltersapply', this.grid, filters, this.grid.getStore()) !== false)
        {
            var storeFilters = this.grid.getStore().filters, filterArr = new Array();
            var exFilters = storeFilters.clone();
            var change = false;
            var active = 0;
            var columns = this.grid.columns;
            var fontArr = [];
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
                    fontArr.push(fn);
                }
            }
            this.changeFont(fontArr,columns);
            this.grid.fireEvent('headerfiltersapply', this.grid, filters, active, this.grid.getStore());
            if(Ext.getCmp('newrownumberer'))
            	Ext.getCmp('newrownumberer').fireEvent('headerfilterschange', this.grid, filters,false);
            var curFilters = this.getFilters();
            this.grid.fireEvent('tooltipchange', this.grid, curFilters, active, this.grid.getStore());
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
    },
    parseFC:function(column, fc){
    	var me = this;
    	var xtype = fc.filtertype||fc.xtype;
    	var dataIndex = column.dataIndex;
    	var width = column.width;
    	if(xtype == 'textfield'){
        	Ext.apply(fc, {
        		originalxtype:xtype,
        		border:false,
    			width:width,
    			emptyText :column.filterJson_.value||'',
    			hideTrigger:true,
    			enableKeyEvents:true,
    			listeners:{
    				focus:function(t){
    					me.displayTrigger(t);
    				},
    				blur:function(t){
    					Ext.defer(function(){
    						me.hideTrigger(t);
        				}, 200);
    				}
    		    },
    		    //去除menu特有的根据鼠标移动焦点
    		    onBlur : function() {
    				var b = this, a = b.focusCls, c = b.inputEl;
    				if (b.destroying) {
    					return
    				}
    				b.beforeBlur();
    				if (a && c) {
    					c.removeCls(a)
    				}
    				if (b.validateOnBlur) {
    					b.validate()
    				}
    				b.hasFocus = false;
    				b.fireEvent("blur", b);
    				b.postBlur()
    			},
		        onTriggerClick:function(){
		        	var mm = this;
		        	if(mm.menu == null){
		        		mm.menu = Ext.create('Ext.menu.Menu', {
			        		bodyStyle:{
			        			padding:0,
			        			'padding-top':'5px'
			        		},
			        		plain: true,
			                style: {
			                    overflow: 'visible'
			                },
			                width:100,
			                dataIndex:column.dataIndex,
			                items: [{
			                	text:'包含',
			                	autoScroll : Boolean,
			                	style: {
            						width: '100px',
            						height:'30px',
            						background:'#fff',
            						margin: '0px',
            						padding:0
        							},
			                	//menuAlign:'left',
			                	canActivate:true,
			                	ignoreParentClicks:true,
			                	menu:Ext.create('Ext.menu.Menu',{
			                		bodyStyle:{
			                		    padding:0,
			                		    background:'#fff'
			                		},
			                		bodyPadding:0,
			                		border:0,
			                		width:120,
			                		height:30,
			                		minWidth:100,
			                		plain: true,
			                		dataIndex:column.dataIndex,
			                	items:[{
			                		labelWidth: 0,
			                		width:120,
			                		height:28,
			                		margin:0,
			                		padding:0,
			                		overflowX:'scroll',
			                		xtype:'textfield',
			                		labelAlign:'left',
			                		dataIndex:dataIndex,
			                		id:dataIndex+'vague',
			                		labelSeparator:"",
			                		border:0,
			                		enableKeyEvents:true,
			                		focusCls: 'x-form-field-cir',
			                		labelStyle:"margin:1;padding:0;height:100%;",
			                		fieldStyle:" background:'#fff';width:100%;height:100%;margin:0px;padding:0px;text-indent:10px;outline:none !important",
			                		enableKeyEvents:true,
			                		listeners:{
			                			keydown:function(field, e){
			                				if(e.getKey()==Ext.EventObject.ENTER){
			                					me.menuKeyEvent(field,'vague',mm);
			                				}
			                			},
			                			blur:function(){
			                					me.hideTrigger(mm);
			                				},
			                				change:function(field,newValue,oldValue){
			                					me.resetother(field,newValue,'textfield',mm);
			                				}
			                		}
			                	}]
			                	})
			                },{
			                	text:'不包含',
			                	autoScroll : Boolean,
			                	style: {
            						width: '100px',
            						height:'30px',
            						background:'#fff',
            						margin: '0px',
            						padding:0
        							},
			                	//menuAlign:'left',
			                	canActivate:true,
			                	ignoreParentClicks:true,
			                	menu:Ext.create('Ext.menu.Menu',{
			                		bodyStyle:{
			                		    padding:0,
			                		    background:'#fff'
			                		},
			                		bodyPadding:0,
			                		border:0,
			                		width:120,
			                		minWidth:100,
			                		plain: true,
			                		iconCls :'menuBackGround',
			                		dataIndex:column.dataIndex,
			                	items:[{
			                		labelWidth: 0,
			                		width:120,
			                		height:28,
			                		margin:0,
			                		padding:0,
			                		overflowX:'scroll',
			                		xtype:'textfield',
			                		labelAlign:'left',
			                		dataIndex:dataIndex,
			                		id:dataIndex+'novague',
			                		labelSeparator:"",
			                		border:0,
			                		enableKeyEvents:true,
			                		focusCls: 'x-form-field-cir',
			                		labelStyle:"background:'#fff';margin:0;padding:0;height:100%",
			                		fieldStyle:" background:'#fff';width:100%;height:100%;margin:0px;padding:0px;text-indent:10px",
			                		enableKeyEvents:true,
			                		listeners:{
			                			keydown:function(field, e){
			                				if(e.getKey()==Ext.EventObject.ENTER){
			                					me.menuKeyEvent(field,'novague',mm);
			                				}
			                			},
			                			blur:function(){
			                					me.hideTrigger(mm);
			                				},
			                			change:function(field,newValue,oldValue){
			                					me.resetother(field,newValue,'textfield',mm);
			                				}
			                		}
			                	}]
			                	})
			                },{
			                	text:'开头是',
			                	autoScroll : Boolean,
			                	style: {
            						width: '100px',
            						height:'30px',
            						background:'#fff',
            						margin: '0px',
            						padding:0
        							},
			                	//menuAlign:'left',
			                	canActivate:true,
			                	ignoreParentClicks:true,
			                	menu:Ext.create('Ext.menu.Menu',{
			                		bodyStyle:{
			                		    padding:0,
			                		    background:'#fff'
			                		},
			                		bodyPadding:0,
			                		border:0,
			                		width:120,
			                		minWidth:100,
			                		plain: true,
			                		iconCls :'menuBackGround',
			                		dataIndex:column.dataIndex,
			                	items:[{
			                		labelWidth: 0,
			                		width:120,
			                		height:28,
			                		margin:0,
			                		padding:0,
			                		overflowX:'scroll',
			                		xtype:'textfield',
			                		labelAlign:'left',
			                		dataIndex:dataIndex,
			                		id:dataIndex+'head',
			                		labelSeparator:"",
			                		border:0,
			                		enableKeyEvents:true,
			                		focusCls: 'x-form-field-cir',
			                		labelStyle:"background:'#fff';margin:0;padding:0;height:100%'",
			                		fieldStyle:"background:'#fff';width:100%;height:100%;margin:0px;padding:0px;text-indent:10px",
			                		enableKeyEvents:true,
			                		listeners:{
			                			keydown:function(field, e){
			                				if(e.getKey()==Ext.EventObject.ENTER){
			                					me.menuKeyEvent(field,'head',mm);
			                				}
			                			},
			                			blur:function(){
			                					me.hideTrigger(mm);
			                				},
			                			change:function(field,newValue,oldValue){
			                					me.resetother(field,newValue,'textfield',mm);
			                				}
			                		}
			                	}]
			                	})
			                },{
			                	text:'结尾是',
			                	autoScroll : Boolean,
			                	style: {
            						width: '100px',
            						height:'30px',
            						background:'#fff',
            						margin: '0px',
            						padding:0
        							},
			                	//menuAlign:'left',
			                	canActivate:true,
			                	ignoreParentClicks:true,
			                	menu:Ext.create('Ext.menu.Menu',{
			                		bodyStyle:{
			                		    padding:0,
			                		    background:'#fff'
			                		},
			                		bodyPadding:0,
			                		border:0,
			                		width:120,
			                		minWidth:100,
			                		plain: true,
			                		iconCls :'menuBackGround',
			                		dataIndex:column.dataIndex,
			                		 
			                	items:[{
			                		labelWidth: 0,
			                		width:120,
			                		height:28,
			                		margin:0,
			                		padding:0,
			                		overflowX:'scroll',
			                		xtype:'textfield',
			                		labelAlign:'left',
			                		dataIndex:dataIndex,
			                		id:dataIndex+'end',
			                		labelSeparator:"",
			                		border:0,
			                		enableKeyEvents:true,
			                		focusCls: 'x-form-field-cir',
			                		labelStyle:"background:'#fff';margin:0;padding:0;height:100%",
			                		fieldStyle:" background:'#fff';width:100%;height:100%;margin:0px;padding:0px;text-indent:10px",
			                		enableKeyEvents:true,
			                		listeners:{
			                			keydown:function(field, e){
			                				if(e.getKey()==Ext.EventObject.ENTER){
			                					me.menuKeyEvent(field,'end',mm);
			                				}
			                			},
			                			blur:function(){
			                					me.hideTrigger(mm);
			                				},
			                			change:function(field,newValue,oldValue){
			                					me.resetother(field,newValue,'textfield',mm);
			                				}
			                		}
			                	}]
			                	})
			                },{
			                	text:'等于',
			                	autoScroll : Boolean,
			                	style: {
            						width: '100px',
            						height:'30px',
            						background:'#fff',
            						margin: '0px',
            						padding:0
        							},
			                	//menuAlign:'left',
			                	canActivate:true,
			                	ignoreParentClicks:true,
			                	menu:Ext.create('Ext.menu.Menu',{
			                		bodyStyle:{
			                		    padding:0,
			                		    background:'#fff'
			                		},
			                		bodyPadding:0,
			                		border:0,
			                		width:120,
			                		minWidth:100,
			                		plain: true,
			                		dataIndex:column.dataIndex,
			                		 
			                	items:[{
			                		labelWidth: 0,
			                		width:120,
			                		height:28,
			                		margin:0,
			                		padding:0,
			                		overflowX:'scroll',
			                		xtype:'textfield',
			                		labelAlign:'left',
			                		dataIndex:dataIndex,
			                		id:dataIndex+'direct',
			                		labelSeparator:"",
			                		border:0,
			                		enableKeyEvents:true,
			                		focusCls: 'x-form-field-cir',
			                		labelStyle:"background:'#fff';margin:0;padding:0;height:100%",
			                		fieldStyle:"background:'#fff';width:100%;height:100%;margin:0px;padding:0px;text-indent:10px",
			                		enableKeyEvents:true,
			                		listeners:{
			                			keydown:function(field, e){
			                				if(e.getKey()==Ext.EventObject.ENTER){
			                					me.menuKeyEvent(field,'direct',mm);
			                				}
			                			},
			                			blur:function(){
			                					me.hideTrigger(mm);
			                				},
			                			change:function(field,newValue,oldValue){
			                					me.resetother(field,newValue,'textfield',mm);
			                				}
			                		}
			                	}]
			                	})
			                },{
			                	text:'不等于',
			                	autoScroll : Boolean,
			                	style: {
            						width: '100px',
            						height:'30px',
            						background:'#fff',
            						margin: '0px',
            						padding:0
        							},
			                	//menuAlign:'left',
			                	canActivate:true,
			                	ignoreParentClicks:true,
			                	menu:Ext.create('Ext.menu.Menu',{
			                		bodyStyle:{
			                		    padding:0,
			                		    background: '#fff'
			                		},
			                		bodyPadding:0,
			                		border:0,
			                		width:120,
			                		minWidth:100,
			                		plain: true,
			                		dataIndex:column.dataIndex,
			                	
			                	items:[{
			                		labelWidth: 0,
			                		width:120,
			                		height:28,
			                		margin:0,
			                		padding:0,
			                		overflowX:'scroll',
			                		xtype:'textfield',
			                		labelAlign:'left',
			                		dataIndex:dataIndex,
			                		id:dataIndex+'nodirect',
			                		labelSeparator:"",
			                		border:0,
			                		enableKeyEvents:true,
			                		focusCls: 'x-form-field-cir',
			                		labelStyle:'margin:0;padding:0;height:100%',
			                		fieldStyle:' background: none;width:100%;height:100%;margin:0px;padding:0px;text-indent:10px',
			                		enableKeyEvents:true,
			                		listeners:{
			                			keydown:function(field, e){
			                				if(e.getKey()==Ext.EventObject.ENTER){
			                					me.menuKeyEvent(field,'nodirect',mm);
			                				}
			                			},
			                			blur:function(){
			                					me.hideTrigger(mm);
			                				},
			                			change:function(field,newValue,oldValue){
			                					me.resetother(field,newValue,'textfield',mm);
			                				}
			                		}
			                	}]
			                	})
			                },{
			                    	text:'空(未填写)',
			                    	labelWidth: 0,
			                    	width:100,
			                    	checked:false,
 			                        dataIndex:dataIndex,
			                        id:dataIndex+'null',
			                        enableKeyEvents:true,
			                        listeners:{
			                        	click:function(field, e){
			                        		me.menuKeyEvent(field,'null',mm);
			                        	}
			                        }
			                    }
			                ]
			            });
		        	}
		        	if(mm.menu&&mm.menu.isHidden()){
		            	var x = mm.el.getX()+mm.getWidth()-19;
		            	mm.menu.showAt(x,mm.el.getY()+mm.getHeight());
		            	for(var i in mm.menu.items.items){
		            		if(i<6){
		            			/*mm.menu.items.items[i].setValue('');
		            			mm.menu.items.items[i].labelEl.dom.style.background = ''*/;
		            			mm.menu.items.items[i].menu.items.items[0].setValue('');
		            			mm.menu.items.items[i].el.dom.style.background = '';
		            		}else{
		            			mm.menu.items.items[i].setChecked(false);
		            		}
		            	}
		            	var value = mm.rawValue==''?mm.emptyText:mm.rawValue;
		            	var field = Ext.getCmp(mm.dataIndex+mm.filterType);
		            	if(mm.rawValue==''&&mm.emptyText!=''&&!mm.fromQuery){
		            		mm.filterType == mm.filterJson_.type;
		            		mm.fromQuery = false;
		            	}
		            	if(mm.filterType == ''){
		            		//var item = mm.menu.items.items[0]
		            		var item = mm.menu.items.items[0].menu.items.items[0];
		            		item.setValue(value);
		            		item.focus(true,100);
		            		if(value != "" && column.textEl.dom.style.fontStyle === "italic"){
		            			item.el.dom.style.background = '#A5A0A0';
		            		}
		            	}else if(mm.filterType=='null'&&(mm.rawValue=='空(未填写)'||mm.emptyText=='空(未填写)')){
		            		field.setChecked(true);
	            		}else if(field.id == mm.id+'null'||value==''){
	            			/*mm.menu.items.items[0].setValue('');
	            			mm.menu.items.items[0].focus(true,100);*/
	            			mm.menu.items.items[0].menu.items.items[0].setValue('');
	            			mm.menu.items.items[0].menu.items.items[0].focus(true,100);
	            				
	            		}else{
		            		field.setValue(value);
		            		field.focus(true,100);
		            		if(value!=''){
		            			field.el.dom.style.background = '#A5A0A0';
		            		}
	            		}
		            	mm.onBlur();
		            }
		        },
		        id:dataIndex+'Filter',
		        value:'',
		        xtype :'trigger'
		    });
        }else if(xtype == 'numberfield'||xtype =='numbercolumn'){
        	Ext.apply(fc, {
        		originalxtype:xtype,
        		border:false,
    			width:width,
    			emptyText :(column.filterJson_.type=='~')?column.filterJson_.value:(column.filterJson_.type+column.filterJson_.value)||'',
    			hideTrigger:true,
    			enableKeyEvents:true,
    			listeners:{
    				focus:function(t){
    					Ext.defer(function(){
    						me.displayTrigger(t);
        				}, 5);
    				},
    				blur:function(t){
    					Ext.defer(function(){
    						me.hideTrigger(t);
        				}, 200);
    				}
    		    },
    		    onBlur : function() {
    				var b = this, a = b.focusCls, c = b.inputEl;
    				if (b.destroying) {
    					return
    				}
    				b.beforeBlur();
    				if (a && c) {
    					c.removeCls(a)
    				}
    				if (b.validateOnBlur) {
    					b.validate()
    				}
    				b.hasFocus = false;
    				b.fireEvent("blur", b);
    				b.postBlur()
    			},
		        onTriggerClick:function(){
		        	var number = this;
		        	if(this.menu == null){
			        	this.menu = Ext.create('Ext.menu.Menu', {
			        		bodyStyle:{
			        			padding:0,
			        			'padding-top':'5px'
			        		},
			        		plain: true,
			                style: {
			                    overflow: 'visible'
			                },
			                width:205,
			                items: [{
			                    	fieldLabel:'等于',
			                    	labelWidth: 60,
			                    	width:200,
			                        xtype:'numberfield',
			                        hideTrigger:true,
			                        labelSeparator:"",
			                        labelAlign:'right',
			                        dataIndex:dataIndex,
			                        id:dataIndex+'=',
			                        enableKeyEvents:true,
			                        labelStyle:'margin-right:0;padding-right:5px;width:65px;',
			                        focusCls: 'x-form-field-cir',
			                        listeners:{
			                        	keydown:function(field, e){
			                        		if(e.getKey()==Ext.EventObject.ENTER){
			                        			me.numberEvent(field,number,'=',number);
			                        		}
			                        	},
			                        	blur:function(){
			            					me.hideTrigger(number);
			            				},
			            				change:function(field,newValue,oldValue){
			            					me.resetother(field,newValue,'numberfield',number);
			            				}
			                        }
			                    },{
			                    	fieldLabel:'不等于',
			                    	labelWidth: 60,
			                    	width:200,
			                        xtype:'numberfield',
			                        hideTrigger:true,
			                        labelSeparator:"",
			                        labelAlign:'right',
			                        dataIndex:dataIndex,
			                        id:dataIndex+'!=',
			                        labelStyle:'margin-right:0;padding-right:5px;width:65px;',
			                        enableKeyEvents:true,
			                        focusCls: 'x-form-field-cir',
			                        listeners:{
			                        	keydown:function(field, e){
			                        		if(e.getKey()==Ext.EventObject.ENTER){
			                        			me.numberEvent(field,number,'!=',number);
			                        		}
			                        	},
			                        	blur:function(){
			            					me.hideTrigger(number);
			            				},
			            				change:function(field,newValue,oldValue){
			            					me.resetother(field,newValue,'numberfield',number);
			            				}
			                        }
			                    },{
			                    	fieldLabel:'大于',
			                    	labelWidth: 60,
			                    	width:200,
			                        xtype:'numberfield',
			                        hideTrigger:true,
			                        labelSeparator:"",
			                        labelAlign:'right',
			                        labelStyle:'margin-right:0;padding-right:5px;width:65px;',
			                        dataIndex:dataIndex,
			                        id:dataIndex+'>',
			                        enableKeyEvents:true,
			                        focusCls: 'x-form-field-cir',
			                        listeners:{
			                        	keydown:function(field, e){
			                        		if(e.getKey()==Ext.EventObject.ENTER){
			                        			me.numberEvent(field,number,'>',number);
			                        		}
			                        	},
			                        	blur:function(){
			            					me.hideTrigger(number);
			            				},
			            				change:function(field,newValue,oldValue){
			            					me.resetother(field,newValue,'numberfield',number);
			            				}
			                        }
			                    },{
			                    	fieldLabel:'大于等于',
			                    	labelWidth: 60,
			                    	width:200,
			                        xtype:'numberfield',
			                        labelAlign:'right',
			                        hideTrigger:true,
			                        labelSeparator:"",
			                        labelStyle:'margin-right:0;padding-right:5px;width:65px;',
			                        dataIndex:dataIndex,
			                        id:dataIndex+'>=',
			                        enableKeyEvents:true,
			                        focusCls: 'x-form-field-cir',
			                        listeners:{
			                        	keydown:function(field, e){
			                        		if(e.getKey()==Ext.EventObject.ENTER){
			                        			me.numberEvent(field,number,'>=',number);
			                        		}
			                        	},
			                        	blur:function(){
			            					me.hideTrigger(number);
			            				},
			            				change:function(field,newValue,oldValue){
			            					me.resetother(field,newValue,'numberfield',number);
			            				}
			                        }
			                    },{
			                    	fieldLabel:'小于',
			                    	labelWidth: 60,
			                    	width:200,
			                        xtype:'numberfield',
			                        hideTrigger:true,
			                        labelAlign:'right',
			                        labelSeparator:"",
			                        dataIndex:dataIndex,
			                        labelStyle:'margin-right:0;padding-right:5px;width:65px;',
			                        id:dataIndex+'<',
			                        focusCls: 'x-form-field-cir',
			                        enableKeyEvents:true,
			                        listeners:{
			                        	keydown:function(field, e){
			                        		if(e.getKey()==Ext.EventObject.ENTER){
			                        			me.numberEvent(field,number,'<',number);
			                        		}
			                        	},
			                        	blur:function(){
			            					me.hideTrigger(number);
			            				},
			            				change:function(field,newValue,oldValue){
			            					me.resetother(field,newValue,'numberfield',number);
			            				}
			                        }
			                    },{
			                    	fieldLabel:'小于等于',
			                    	labelWidth: 60,
			                    	width:200,
			                        xtype:'numberfield',
			                        hideTrigger:true,
			                        labelAlign:'right',
			                        labelSeparator:"",
			                        dataIndex:dataIndex,
			                        id:dataIndex+'<=',
			                        enableKeyEvents:true,
			                        focusCls: 'x-form-field-cir',
			                        listeners:{
			                        	keydown:function(field, e){
			                        		if(e.getKey()==Ext.EventObject.ENTER){
			                        			me.numberEvent(field,number,'<=',number);
			                        		}
			                        	},
			                        	blur:function(){
			            					me.hideTrigger(number);
			            				},
			            				change:function(field,newValue,oldValue){
			            					me.resetother(field,newValue,'numberfield',number);
			            				}
			                        }
			                    },{
			                    	width:200,
			                        xtype:'fieldcontainer',
			                        dataIndex:dataIndex,
			                        id:dataIndex+'~',
			                        enableKeyEvents:true,
			                        layout: 'column',
			                        items:[{
			                        	fieldLabel:'介于',
			                        	labelWidth: 60,
			                        	columnWidth: 0.63,
			                        	labelAlign:'right',
				                        labelSeparator:"",
				                        xtype:'numberfield',
				                        dataIndex:dataIndex,
				                        enableKeyEvents:true,
				                        id:dataIndex+'between1',
				                        hideTrigger:true,
				                        focusCls: 'x-form-field-cir',
				                        listeners:{
			                        	 keydown:function(field, e){
			                        		if(e.getKey()==Ext.EventObject.ENTER){
			                        			var behind = Ext.getCmp(this.dataIndex+'between2');
			                        			var value = field.getValue();
			                        			if((value==0||value != '')&&value!=null&&(behind.value==0||behind.value != '')&&behind.value!=null){
			                        				if(value>behind.value){
			                        					showError('前面的数字不能大于后面的数字!');
			                        					return;
			                        				}
			                        				number.setValue(value+'~'+behind.value);
			                        				number.filterType = '~';
			                        				me.applyFilters();
			                        				field.ownerCt.ownerCt.hide();
			                        			}else if((value == null || (value == ''&&value!=0))&&((behind.value!=0&&behind.value == '')||behind.value==null)){
			                        				number.setValue('');
			                        				number.emptyText = '';
			                        				number.inputEl.dom.placeholder = '';
			                        				me.applyFilters();
			                        				field.ownerCt.ownerCt.hide();
			                        			}
			                        		}
			                        	},
			                        	blur:function(){
			            					me.hideTrigger(number);
			            				},
			            				change:function(field,newValue,oldValue){
			            					me.resetother(field,newValue,'numberfield',number);
			            				}
			                        }
			                    },{
			                    	fieldLabel:'-',
			                    	columnWidth: 0.37,
			                    	labelWidth: 10,
			                    	labelSeparator :'',
			                        xtype:'numberfield',
			                        labelSeparator:"",
			                        dataIndex:dataIndex,
			                        enableKeyEvents:true,
			                        id:dataIndex+'between2',
			                        hideTrigger:true,
			                        focusCls: 'x-form-field-cir',
			                        listeners:{
			                        	keydown:function(field, e){
			                        		if(e.getKey()==Ext.EventObject.ENTER){
			                        			var before = Ext.getCmp(this.dataIndex+'between1');
			                        			var value = field.getValue();
			                        			if((value==0||value != '')&&value!=null&&(before.value==0||before.value != '')&&before.value!=null){
			                        				if(value<before.value){
			                        					showError('前面的数字不能大于后面的数字!');
			                        					return;
			                        				}
			                        				number.setValue(before.value+'~'+value);
			                        				number.filterType = '~';
			                        				me.applyFilters();
			                        				field.ownerCt.ownerCt.hide();
			                        			}else if((value == null || (value == ''&&value!=0))&&((before.value!=0&&before.value == '')||before.value==null)){
			                        				number.setValue('');
			                        				number.emptyText = '';
			                        				number.inputEl.dom.placeholder = '';
			                        				me.applyFilters();
			                        				field.ownerCt.ownerCt.hide();
			                        			}
			                        		}
			                        	},
			                        	blur:function(){
			            					me.hideTrigger(number);
			            				},
			            				change:function(field,newValue,oldValue){
			            					me.resetother(field,newValue,'numberfield',number);
			            				}
			                        }
			                    }]
			                    }
			                ]
			            });
		        	}
		        	if(this.menu&&this.menu.isHidden()){
		            	var x = this.el.getX()+this.width-19;
		            	this.menu.showAt(x,this.el.getY()+this.getSize().height);
		            	var value = this.getValue()==''?this.emptyText:this.getValue();
		            	for(var i in this.menu.items.items){
		            		var item = this.menu.items.items[i];
		            		if(i==6){
		            			item.items.items[0].setValue('');
		            			item.items.items[1].setValue('');
		            			item.items.items[0].el.dom.style.background = '';
		            		}else{
		            			item.setValue('');
		            			item.el.dom.style.background = '';
		            		}
		            	}
		            	var it = this.menu.items;
		            	//下拉时将筛选头中的值赋给下拉中对应的输入框并获取焦点
		            	if(value.indexOf('>=')==0){
		            		me.focusItem(it.items[3],'>=',value,this,column);
		            	}else if(value.indexOf('<=')==0){
		            		me.focusItem(it.items[5],'<=',value,this,column);
		            	}else if(value.indexOf('>')==0){
		            		me.focusItem(it.items[2],'>',value,this,column);
		            	}else if(value.indexOf('<')==0){
		            		me.focusItem(it.items[4],'<',value,this,column);
		            	}else if(value.indexOf('!=')==0){
		            		me.focusItem(it.items[1],'!=',value,this,column);
		            	}else if(value.indexOf('~')>-1){
		            		var values = value.split('~');
		            		it.items[6].items.items[0].setValue(values[0]);
		            		it.items[6].items.items[1].setValue(values[1]);
		            		it.items[6].items.items[0].focus(true,100);
		            		it.items[6].items.items[0].el.dom.style.background = '#A5A0A0';
		            	}else if(value.indexOf('=')>-1){
		            		me.focusItem(it.items[0],'=',value,this,column);
		            	}else{
		            		me.focusItem(it.items[0],'',value,this,column);
		            	}
		            }
		        },
		        id:dataIndex+'Filter',
		        value:'',
		        xtype :'trigger'
		    });
        }else if(xtype == 'datefield'||xtype == 'datetimefield'){
        	var wid = column.width;
        	if (wid < 26) {
                column.on('resize', function(field,newValue){
                	var t = Ext.getCmp(field.dataIndex + 'Filter');
                	if((t.xtype == 'btndatefield')&&t.hideTrigger==true){
            			var width = parseInt(t.getWidth());
            			t.hideTrigger = false;
            			t.inputEl.dom.style.width = (Number(width) - 18)+'px';
            			t.triggerEl.item(1).dom.parentNode.style.width = '17px';
            			t.triggerEl.item(1).dom.parentNode.style.display = 'block';
            			t.triggerEl.item(1).setDisplayed('block');
            			t.triggerEl.item(1).setWidth(17);
            		}
                });
        		var need = true;
        	}
        	Ext.apply(fc, {
        		originalxtype: 'datefield',
        		border: false,
        		filterContainerId: column.id + '-filtersContainer',
    			width: column.width,
    			emptyText: column.filterJson_.type=='~'?column.filterJson_.value:(column.filterJson_.type+column.filterJson_.value)||'',
    			dataIndex: column.dataIndex,
    			hideTrigger: need,
    			enableKeyEvents: true,
		        id: dataIndex+'Filter',
		        xtype: 'btndatefield',
		        listeners: {
		        	'dateclick':function(){
		        		me.applyFilters();
		        	},
		        	change: function(){
		        		if(this.BtnPicker&&!this.BtnPicker.isHidden()){
		        			this.BtnPicker.hide();
		        			this.BtnPicker2.hide();
		        		}
		        	}
		        }
		    });
        }
    },
    //textfield类型的下拉ENTER事件操作
    menuKeyEvent:function(field,filterType,owner){
    	var me = this;
    	var menu = field.ownerCt;
    	if(filterType=='null'){
			if(field.checked){
				owner.setValue(field.text);
				owner.filterType = 'null';
				owner.filterSelect = true;
				//owner.inputEl.dom.disabled="disabled"; //不可更改值
    			owner.inputEl.dom.style.background="#C8C8C8";
			}else{
				owner.setValue('');
				owner.inputEl.dom.disabled="";
    			owner.inputEl.dom.style.background="#eee";
			}
    	}else{
    		var value = field.getValue();
    		if(value!=''&&filterType!='vague'){
	    		//owner.inputEl.dom.disabled="disabled";
		    	owner.inputEl.dom.style.background="#C8C8C8";
    		}else{
    			owner.inputEl.dom.disabled="";
		    	owner.inputEl.dom.style.background="#eee";
    		}
    		owner.setRawValue(value);
	    	owner.filterType = filterType;
	    	owner.filterSelect = true;
	    	if(value==''){
	    		field.el.dom.style.background = '';
	    		owner.emptyText = '';
    			owner.inputEl.dom.placeholder = '';
	    	}else{
	    		field.el.dom.style.background = '#A5A0A0';
	    	}
    	}
    	//清空下拉中除了触发ENTER事件的textfield的其他中的值
		for(var i in me.textfieldArr){
			var id = menu.dataIndex+me.textfieldArr[i];
			var another = Ext.getCmp(id);
			if(me.textfieldArr[i] == 'null'&&filterType!='null'){
				another.setChecked(false);
			}else if(another&&id!=field.id&&another.value&&(another.value!=''||another.value!=null)){
				another.setValue('');
			}
		}
		me.applyFilters();
		menu.hide();
		me.displayTrigger(owner);
    },
    //numberfield ENTER事件处理
    numberEvent:function(field,t,type,numberTrigger){
    	var me = this,value = field.getValue(),menu = field.ownerCt;
		if(value!=null){
			t.setValue(type+field.getValue());
		}else{
			t.setValue('');
			t.emptyText = '';
			t.inputEl.dom.placeholder = '';
			field.fireEvent('blur');
			var width = numberTrigger.inputEl.dom.style.width.split('px')[0];
			numberTrigger.inputEl.dom.style.width = (Number(width) + 17)+'px';
			numberTrigger.triggerEl.item(0).setDisplayed('none');
		}
		numberTrigger.filterType = type;
		me.applyFilters();
		menu.hide();
    },
    focusItem:function(item, type, value, numberTrigger,column){
    	if(type!=''){
    		item.setValue(value.split(type)[1]);
    		numberTrigger.filterType = type;
    	}else{
    		item.setValue(value);
    	}
    	item.focus(true,100);
    	if(value != "" && column.textEl.dom.style.fontStyle === "italic"){
    		item.el.dom.style.background = '#A5A0A0';
    	}
    },
    //显示筛选头的下拉箭头
    displayTrigger:function(t){
    	if(t.getWidth()>17&&(!t.triggerEl.item(0).isDisplayed()||t.triggerEl.item(0).dom.style.display==''||(t.getWidth()-parseInt(t.inputEl.dom.style.width)<17))){
			var width = parseInt(t.getWidth());
			t.inputEl.dom.style.width = (Number(width) - 18)+'px';
			t.triggerEl.item(0).dom.parentNode.style.width = '17px';
			t.triggerEl.item(0).dom.parentNode.style.display = 'block';
			t.triggerEl.item(0).setDisplayed('block');
			t.triggerEl.item(0).setWidth(17);
		}
    },
    //隐藏筛选头的下拉箭头
    hideTrigger:function(t){
    	if(document.activeElement.id!=t.inputEl.dom.id&&t.triggerEl.item(0).dom.style.display=='block'&&(!t.menu||(t.menu&&t.menu.isHidden()))&&(t.rawValue==''||t.rawValue==null)){
			var width = t.inputEl.dom.style.width.split('px')[0];
			t.inputEl.dom.style.width = (Number(width) + 17)+'px';
			t.triggerEl.item(0).setDisplayed('none');
		}
    },
    //清空不是当前筛选条件的值，并标识
    resetother:function(field,newValue,type,ownerCt){
    	var me = this,menu = field.ownerCt,filterType = ownerCt.filterType;;
    	if(newValue!=''&&newValue!=null){
    		if(type=='textfield'){
		    	for(var i in me.textfieldArr){
					var id = menu.dataIndex+me.textfieldArr[i];
					var another = Ext.getCmp(id);
					if(filterType==''){
						filterType = 'vague';
					}
					if(another&&id!=field.id&&me.textfieldArr[i]!=filterType&&another.value&&(another.value!=''||another.value!=null)){
						another.setValue('');
					}
				}
    		}else if(type=='numberfield'){
    			var dataIndex = field.dataIndex;
    			for(var i in me.numberfieldArr){
    				var id = dataIndex+me.numberfieldArr[i];
    				var another = Ext.getCmp(id);
					if(filterType==''){
						filterType = '=';
					}
    				if(another&&id!=field.id&&another.value&&me.numberfieldArr[i]!=filterType&&(another.value!=''||another.value!=null)){
    					another.setValue('');
    				}
    			}
    			if(filterType!='~'&&field.id!=dataIndex+'between1' && field.id!=dataIndex+'between2'){
    				Ext.getCmp(dataIndex+'between1').setValue('');
    				Ext.getCmp(dataIndex+'between2').setValue('');
    			}
    		}
    	}
    }
});

