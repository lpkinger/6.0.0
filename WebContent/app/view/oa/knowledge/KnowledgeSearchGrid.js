Ext.require([
    'erp.util.*' 
]);
Ext.define('erp.view.oa.knowledge.KnowledgeSearchGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpSearchGridPanel',
	layout : 'auto',
	id: 'grid', 
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: new Array(),
    searchValue: null,
    
    /**
     * @private
     * The row indexes where matching strings are found. (used by previous and next buttons)
     */
    indexes: [],
    
    /**
     * @private
     * The row index of the first search, it could change if next or previous buttons are used.
     */
    currentIndex: null,
    
    /**
     * @private
     * The generated regular expression used for searching.
     */
    searchRegExp: null,
    
    /**
     * @private
     * Case sensitive mode.
     */
    caseSensitive: false,
    
    /**
     * @private
     * Regular expression mode.
     */
    regExpMode: false,
    
    /**
     * @cfg {String} matchCls
     * The matched string css classe.
     */
    matchCls: 'x-livesearch-match',
    
    defaultStatusText: 'Nothing Found',
    bodyStyle:'background-color:#f1f1f1;',
    selModel: Ext.create('Ext.selection.CheckboxModel',{
    	headerWidth: 0
	}),
	dockedItems: [{
                 xtype: 'toolbar',
                 dock: 'top',
                 style:'background:#CDCDB4;height:45px;font-size:16px;',
			     bodyStyle: 'background:#CDCDB4; padding:0px;font-size:16px;',
                 items: [{
                 xtype: 'textfield',
                 fieldLabel:'<h1>全文检索:</h1>',
                 labelSeparator:"",
                 width:300,
	             name:'search',
	             id:'search',
	             labelStyle:'font-size:16px',
	             fieldStyle : 'background:#DEDEDE;width:200px;font-size:16px;height:22px',
            }, {
                xtype: 'button',
                margin: '0 0 0 4px',
                text: '<',
                id:'prev',
                tooltip: '上一行',
            },{
                xtype: 'button',
                margin: '0 0 0 4px',
                 id:'next',
                text: '>',
                tooltip: '下一行',
            }, '-', {
                xtype: 'checkbox',          
                margin: '0 0 0 4px',
                id:'regular',
                hideLabel:true,                      
            },'<span style="font-size:15px;">正规表达式</span>', {
                xtype: 'checkbox',
                hideLabel: true,
                id:'case',
                margin: '0 0 0 4px',          
            }, '<span style="font-size:14px;">区分大小写</span>','->',
            {
             xtype: 'textfield',
             fieldLabel:'<h1>匹配数:</h1>',
             labelAlign:'right', 
             id:'matchs',
             emptyText:'未找到匹配值',
             labelSeparator:"",
             readOnly:true,
             fieldStyle : 'background:#CDCDB4 ;border-bottom-style:1px solid;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;color:#CD661D;border-bottom-style:1px solid;border-left:none; ',
            },'-',{
              xtype:'button',
              id:'previous',
               iconCls:'prev',
              disabled:true,
             },{
              xtype:'button',
              id:'nextone',
              iconCls:'next',
              disabled:true,
              style:'margin-right:20px;margin-left:5px;'
             }],
	             },{
    	           id : 'pagingtoolbar',
                   xtype: 'erpDatalistToolbar',
                   dock: 'bottom',
                 displayInfo: true
	           }],
    plugins: [Ext.create('Ext.ux.grid.GridHeaderFilters')],
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	RenderUtil:Ext.create('erp.util.RenderUtil'),
	tagsRe: /<[^>]*>/gm,
    tagsProtect: '\x0f',
    regExpProtect: /\\|\/|\+|\\|\.|\[|\]|\{|\}|\?|\$|\*|\^|\|/gm,
	initComponent : function(){ 
		condition = this.BaseUtil.getUrlParam('urlcondition');
		condition = (condition == null) ? "" : condition;
		condition = condition.replace(/@/,"'%").replace(/@/,"%'");
		this.defaultCondition = condition;//固定条件；从url里面获取
    	caller = this.BaseUtil.getUrlParam('whoami');
		this.getCount(caller, condition);
		this.callParent(arguments); 
	} ,
	getColumnsAndStore: function(c, d, g, s){
		c = c || caller;
		d = d || condition;
		g = g || page;
		s = s || pageSize;
		var me = this;
		var f = d;
		if(me.filterCondition){
			if(d == null || d == ''){
				f = me.filterCondition;
			} else {
				f += ' AND ' + me.filterCondition;
			}
		}
		me.BaseUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'common/datalist.action',
        	params: {
        		caller: c,
        		condition:  f, 
        		page: g,
        		pageSize: s
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		me.BaseUtil.getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}
        		var data = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : [];//一定要去掉多余逗号，ie对此很敏感
        		if(me.columns && me.columns.length > 2){
        			me.store.loadData(data);
        			if(me.store.data.items.length != data.length){
        				me.store.add(data);
        			}
        			if(me.lastSelected && me.lastSelected.length > 0){//grid刷新后，仍然选中上次选中的record
            			Ext.each(me.store.data.items, function(item){
            				if(item.data[keyField] == me.lastSelected[0].data[keyField]){
            					me.selModel.select(item);
            				}
            			});
            		}
        		} else {
        			var store = Ext.create('Ext.data.Store', {
            		    fields: res.fields,
            		    data: data
            		});
    				//处理render
        			var grid = this;
                    Ext.Array.each(res.columns, function(column, y) {   
        				if(!column.haveRendered && column.renderer != null && column.renderer != ""){
        					if(!grid.RenderUtil){
        						grid.RenderUtil = Ext.create('erp.util.RenderUtil');
        					}
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
                    			if(!grid.RenderUtil.args[renderName]){
                    				grid.RenderUtil.args[renderName] = new Object();
                    			}
                    			grid.RenderUtil.args[renderName][column.dataIndex] = args;
                    			//这里只能用column.dataIndex来标志，不能用x,y,index等，
                    			//grid在render时，checkbox占一列
                    		}
                    		column.renderer = grid.RenderUtil[renderName];
                    		column.haveRendered = true;
                    	}
                    });            
            		me.reconfigure(store, res.columns);//用这个方法每次都会add一个checkbox列
        		}
        		//修改pagingtoolbar信息
        		Ext.getCmp('pagingtoolbar').afterOnLoad();
        		//拿到datalist对应的单表的关键词
        		keyField = res.keyField;//form表主键字段
        		pfField = res.pfField;//grid表主键字段
        		url = basePath + res.url;//grid行选择之后iframe嵌入的页面链接
        		relative = res.relative;
        		if(res.vastbutton && res.vastbutton == 'erpAddButton'){//[新增]功能
        			Ext.getCmp('erpAddButton').show();
        		}
        	}
        });
	},
	getCount: function(c, d){
		c = c || caller;
		d = d || condition;
		var me = this;
		var f = d;
		if(me.filterCondition){
			if(d == null || d == ''){
				f = me.filterCondition;
			} else {
				f += ' AND ' + me.filterCondition;
			}
		}
		Ext.Ajax.request({//拿到grid的数据总数count
        	url : basePath + '/common/datalistCount.action',
        	params: {
        		caller: c,
        		condition: f,
        		_noc:1
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}
        		dataCount = res.count;
        		me.getColumnsAndStore(c, d);
        	}
        });
	},
	listeners: {
        'headerfiltersapply': function(grid, filters) {
        	if(this.allowFilter){
        		var condition = null;
                for(var fn in filters){
                    var value = filters[fn];
                    if(!Ext.isEmpty(value)){
                    	if(Ext.isDate(value)){
                    		value = Ext.Date.toString(value);
                    		value = fn + "=to_date('" + value + "','yyyy-MM-dd') ";
                    	} else {
                    		var exp_t = /^(\d{4})\-(\d{2})\-(\d{2}) (\d{2}):(\d{2}):(\d{2})$/,
                    		exp_d = /^(\d{4})\-(\d{2})\-(\d{2})$/;
	                    	if(exp_d.test(value)){
	                    		value = fn + "=to_date('" + value + "','yyyy-MM-dd') ";
	                    	} else if(exp_t.test(value)){
	                    		value = fn + "=to_date('" + value + "','yyyy-MM-dd HH24:mi:ss') ";
	                    	} else{
	                    		value = fn + " LIKE '%" + value + "%' ";
	                    	}
                    	}
                    	if(condition == null){
                    		condition = value;
                    	} else {
                    		condition = condition + " AND " + value;
                    	}
                    }
                }
                this.filterCondition = condition;
                page = 1;
                this.getCount();
        	} else {
        		this.allowFilter = true;
        	}
        	return false;
        }
    },
    reconfigure: function(store, columns){
    	//改写reconfigure方法
    	var d = this.headerCt;
    	if (this.columns.length <= 1 && columns) {//this.columns.length > 1表示grid的columns已存在，没必要remove再add
    		if(!Ext.isChrome){//ie,firefox下，format出现NaN-NaN-NaN,暂时作string处理
    			Ext.each(columns, function(c){
    				if(c.xtype == 'datecolumn'){
    					c.xtype = "";
    					c.format = "";
    				}
    			});
    		}
			d.suspendLayout = true;
			d.removeAll();
			d.add(columns);
		}
		if (store) {
			if(!Ext.isChrome){//ie,firefox下，format出现NaN-NaN-NaN
				Ext.each(store.fields, function(f){
					if(f.type == 'date'){
						f.type = "string";
						f.format = "";
					}
				});
			}
			this.bindStore(store);
		} else {
			this.getView().refresh();
		}
		if (columns) {
			d.suspendLayout = false;
			this.forceComponentLayout();
		}
		this.fireEvent("reconfigure", this);
    },
    getSearchValue: function() {
        var me = this,
            value = Ext.getCmp('search').getValue();
            
        if (value === '') {
            return null;
        }
        if (!me.regExpMode) {
            value = value.replace(me.regExpProtect, function(m) {
                return '\\' + m;
            });
        } else {
            try {
                new RegExp(value);
            } catch (error) {
                me.statusBar.setStatus({
                    text: error.message,
                    iconCls: 'x-status-error'
                });
                return null;
            }
            // this is stupid
            if (value === '^' || value === '$') {
                return null;
            }
        }

        return value;
    },
    
});