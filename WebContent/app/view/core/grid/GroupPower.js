/**
 * ERP项目groupgrid样式:hrjob分组
 */
Ext.define('erp.view.core.grid.GroupPower',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.grouppower',
	requires: ['erp.view.core.grid.HeaderFilter'],
	layout : 'fit',
	id: 'grid',
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    iconCls: 'icon-grid',
    frame: true,
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    bodyStyle:'background-color:#f1f1f1;',
    features: [Ext.create('Ext.grid.feature.Grouping',{
    	disabled :(joborgnorelation && joborgnorelation=="true") ?true:false,
    	startCollapsed: false,
        groupHeaderTpl: '{name} ({rows.length})'
    })],
    plugins: [Ext.create('erp.view.core.grid.HeaderFilter',{
    applyFilters: function()
    {	
        var me = this, filters = this.parseFilters();
         var i=0;
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
                    	var args = arguments.callee.caller.caller.caller.arguments[0];
                    	var d = item.data;
                    	var r = args[0] ? args[0].filterArr:args.filters[0].filterArr;
                    	for(j in r) {
                    		var n = r[j].property, v = r[j].value;
                    		v = me.ignoreCase ? v.toUpperCase() : v;
                    		if(!Ext.isEmpty(d[n])) {
                    			var _v = d[n];
                    			if(Ext.isDate(_v)) {
                    				_v = Ext.Date.toString(_v);
                    			}
                    			_v = me.ignoreCase ? _v.toUpperCase() : _v;
                    			/**
                    			 * @author lidy
                    			 * 兼容多条件筛选
                    			 */
                    			if (v.indexOf('#') == -1){
	                        		if (String(_v).indexOf(v) == -1)
	                        			return false;
                    			}else{
                    				v = v.trim().replace(/^#/,'').replace(/#$/,'');
                    				if(Ext.isEmpty(v)){
                    					return true;
                    				}
                    				var arr = v.split('#');
                    				for(var i = 0 ; i < arr.length ; i++){
                    					if(!Ext.isEmpty(arr[i])){
                    						if(String(_v).indexOf(arr[i]) != -1){
                    							return true;
                    						}
                    					}
                    				}
                    				return false;
                    			}
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
    }
    })],
    store: Ext.create('Ext.data.Store', {
    	fields: [{
    		name: 'em_id',
    		type: 'number'
    	},{
        	name: 'jo_orgid',
        	type: 'number'
        },{
        	name:'jo_orgname',
        	type:'string'
        },{
        	name:'jo_description',
        	type:'string'
        },{
        	name:'jo_name',
        	type:'string'
        },{
        	name:'jo_id',
        	type:'number'
        },{
        	name:'ro_id',
        	type:'number'
        },{
        	name:'ro_name',
        	type:'string'
        },{
        	name:'pp_see',
        	type:'bool'
        },{
        	name:'pp_selflist',
        	type:'bool'
        },{
        	name:'pp_alllist',
        	type:'bool'
        },{
        	name:'pp_add',
        	type:'bool'
        },{
        	name:'pp_delete',
        	type:'bool'
        },{
        	name:'pp_save',
        	type:'bool'
        },{
        	name:'pp_saveoth',
        	type:'bool'
        },{
        	name:'pp_commit',
        	type:'bool'
        },{
        	name:'pp_uncommit',
        	type:'bool'
        },{
        	name:'pp_audit',
        	type:'bool'
        },{
        	name:'pp_unaudit',
        	type:'bool'
        },{
        	name:'pp_print',
        	type:'bool'
        },{
        	name:'pp_printoth',
        	type:'bool'
        },{
        	name:'pp_printrepeat',
        	type:'bool'
        },{
        	name:'pp_disable',
        	type:'bool'
        },{
        	name:'pp_undisable',
        	type:'bool'
        },{
        	name:'pp_closed',
        	type:'bool'
        },{
        	name:'pp_unclosed',
        	type:'bool'
        },{
        	name:'pp_posting',
        	type:'bool'
        },{
        	name:'pp_unposting',
        	type:'bool'
        },{
        	name:'pp_jobemployee',
        	type:'bool'
        },{
        	name: 'pp_more',
        	type: 'string'
        }],
        sorters: [{
            property : 'jo_id',
            direction: 'ASC'
        }],
        groupField:'jo_orgname'
    }),
    basicPowerColumns:[{
    	text: '操作名称',
    	columns: [{
        	text: '<br>新增',
        	width: 45,
        	cls: 'x-grid-header',
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'pp_add'
        },{
        	text: '<br>浏览',
        	width: 45,
        	cls: 'x-grid-header',
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'pp_see'
        },{
        	text: '<br>删除',
        	width: 45,
        	cls: 'x-grid-header',
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'pp_delete'
        },{
        	text: '<br>修改',
        	width: 45,
        	cls: 'x-grid-header',
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'pp_save'
        },{
        	text: '修<br/>改他人',
        	width: 45,
        	cls: 'x-grid-header',
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'pp_saveoth'
        },{
        	text: '<br>提交',
        	width: 45,
        	cls: 'x-grid-header',
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'pp_commit'
        },{
        	text: '反<br/>提交',
        	width: 45,
        	cls: 'x-grid-header',
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'pp_uncommit'
        },{
        	text: '<br>审核',
        	width: 45,
        	cls: 'x-grid-header',
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'pp_audit'
        },{
        	text: '反<br/>审核',
        	width: 45,
        	cls: 'x-grid-header',
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'pp_unaudit'
        },{
        	text: '<br>打印',
        	width: 45,
        	cls: 'x-grid-header',
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'pp_print'
        },{
        	text: '打印<br/>他人',
        	width: 45,
        	cls: 'x-grid-header',
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'pp_printoth'
        },{
        	text: '多次<br/>打印',
        	width: 45,
        	cls: 'x-grid-header',
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'pp_printrepeat'
        },{
        	text: '<br>禁用',
        	width: 45,
        	cls: 'x-grid-header',
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'pp_disable'
        },{
        	text: '反<br/>禁用',
        	width: 45,
        	cls: 'x-grid-header',
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'pp_undisable'
        },{
        	text: '<br>操作',
        	width: 45,
        	cls: 'x-grid-header',
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'pp_closed'
        },{
        	text: '反<br/>操作',
        	width: 45,
        	cls: 'x-grid-header',
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'pp_unclosed'
        },{
        	text: '<br>过账',
        	width: 45,
        	cls: 'x-grid-header',
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'pp_posting'
        },{
        	text: '反<br/>过账',
        	width: 45,
        	cls: 'x-grid-header',
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'pp_unposting'
        },{
        	text: '更多',
        	width: 45,
        	cls: 'x-grid-header',
        	dataIndex: 'pp_more',
        	xtype: 'numbercolumn',
        	renderer: function(val, meta){
        		meta.tdCls = 'x-grid-search-trigger';
        		meta.style = 'cursor:pointer;';
        		return val;
        	},
        	processEvent: function(type, view, cell, recordIndex, cellIndex, e) {
        		if (type == 'mousedown' || (type == 'keydown' && (e.getKey() == e.ENTER || e.getKey() == e.SPACE))) {
        			var grid = view.ownerCt, record = grid.store.getAt(recordIndex);
        			grid.setMore(grid.pp_caller, record.data.jo_id,record.data.ro_id);
        		}
        		return false;
        	}
        },{
        	text: '特殊',
        	width: 45,
        	cls: 'x-grid-header',
        	dataIndex: 'pp_special',
        	xtype: 'numbercolumn',
        	renderer: function(val, meta){
        		meta.tdCls = 'x-grid-search-trigger';
        		meta.style = 'cursor:pointer;';
        		return val;
        	},
        	processEvent: function(type, view, cell, recordIndex, cellIndex, e) {
        		if (type == 'mousedown' || (type == 'keydown' && (e.getKey() == e.ENTER || e.getKey() == e.SPACE))) {
        			var grid = view.ownerCt, record = grid.store.getAt(recordIndex);
        			grid.setSpecial(grid.pp_caller, record.data.jo_id,record.data.ro_id);
        		}
        		return false;
        	}
        }]
    }],
    jobColumns:[{
        text: 'ID',
        hidden: true,
        dataIndex: 'jo_id'
    },{
        text: '组织ID',
        hidden: true,
        dataIndex: 'jo_orgid'
    },{
        text: '组织名称',
        hidden: true,
        dataIndex: 'jo_orgname'
    },{
        text: '岗位名称',
        width: 220,
        cls: 'x-grid-header',
        dataIndex: 'jo_name',
        filter: {xtype: 'textfield', filterName: 'jo_name'},
        setPadding: Ext.emptyFn
    }],
    roleDefaultColumns:null,
    roleListColumns:null,
    roleDealColumns:null,
    roleColumns:[{
        text: 'RO_ID',
        hidden: true,
        dataIndex: 'ro_id'
    },{
        text: '角色名称',
        width: 220,
        cls: 'x-grid-header',
        dataIndex: 'ro_name',
        filter: {xtype: 'multidbfindtrigger', filterName: 'ro_name',id: 'ro_name', name: 'ro_name'},
        setPadding: Ext.emptyFn
    }],
    defaultColumns:null,
    basicListColumns:[{
    	text: '操作名称',
    	columns: [{
        	text: '浏<br>览自己',
        	width: 45,
        	cls: 'x-grid-header',
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'pp_selflist'
        },{
        	text: '浏览<br>岗位下属',
        	width: 56,
        	cls: 'x-grid-header',
            xtype: 'checkcolumn',
            hidden:!defaultHrJobPowerExists,
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'pp_jobemployee'
        },{
        	text: '浏<br>览所有',
        	width: 45,
        	cls: 'x-grid-header',
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'pp_alllist'
        }]
    },{
    	text: '更多',
    	width: 45,
    	cls: 'x-grid-header',
    	dataIndex: 'pp_more',
    	xtype: 'numbercolumn',
    	renderer: function(val, meta){
    		meta.tdCls = 'x-grid-search-trigger';
    		meta.style = 'cursor:pointer;';
    		return val;
    	},
    	processEvent: function(type, view, cell, recordIndex, cellIndex, e) {
    		if (type == 'mousedown' || (type == 'keydown' && (e.getKey() == e.ENTER || e.getKey() == e.SPACE))) {
    			var grid = view.ownerCt, record = grid.store.getAt(recordIndex);
    			grid.setMore(grid.pp_caller, record.data.jo_id,record.data.ro_id);
    		}
    		return false;
    	}
    }],
    dealColumns:null,
    basicDealColumns: [{
    	text: '操作名称',
    	columns: [{
        	text: '浏览',
        	width: 45,
        	cls: 'x-grid-header',
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'pp_see'
        }]
    },{
    	text: '特殊',
    	width: 45,
    	cls: 'x-grid-header',
    	dataIndex: 'pp_special',
    	xtype: 'numbercolumn',
    	renderer: function(val, meta){
    		meta.tdCls = 'x-grid-search-trigger';
    		meta.style = 'cursor:pointer;';
    		return val;
    	},
    	processEvent: function(type, view, cell, recordIndex, cellIndex, e) {
    		if (type == 'mousedown' || (type == 'keydown' && (e.getKey() == e.ENTER || e.getKey() == e.SPACE))) {
    			var grid = view.ownerCt, record = grid.store.getAt(recordIndex);
    			grid.setSpecial(grid.pp_caller, record.data.jo_id,record.data.ro_id);
    		}
    		return false;
    	}
    }],
    benchColumns: [{
        text: 'ID',
        hidden: true,
        dataIndex: 'jo_id'
    },{
        text: '组织ID',
        hidden: true,
        dataIndex: 'jo_orgid'
    },{
        text: '组织名称',
        hidden: true,
        dataIndex: 'jo_orgname'
    },{
        text: '岗位名称',
        width: 220,
        cls: 'x-grid-header',
        dataIndex: 'jo_name',
        filter: {xtype: 'textfield', filterName: 'jo_name'},
        setPadding: Ext.emptyFn
    },{
    	text: '场景<br>按钮',
    	width: 45,
    	cls: 'x-grid-header',
    	dataIndex: 'bp_special',
    	xtype: 'numbercolumn',
    	renderer: function(val, meta){
    		meta.tdCls = 'x-grid-search-trigger';
    		meta.style = 'cursor:pointer;';
    		return val;
    	},
    	processEvent: function(type, view, cell, recordIndex, cellIndex, e) {
    		if (type == 'mousedown' || (type == 'keydown' && (e.getKey() == e.ENTER || e.getKey() == e.SPACE))) {
    			var grid = view.ownerCt, record = grid.store.getAt(recordIndex);
    			grid.showScenBtn(record.data.jo_id);
    		}
    		return false;
    	}
    }],
    showScenBtn: function(jo_id) {
		var grid = this, cal = grid.pp_caller,self = grid._self;
		function showButton(value,cellmeta){
			if(jo_id){
				var returnStr = "<INPUT align='center' type='button' value='删除' onclick='Delete(&quot;"+cal+"&quot;,"+self+","+jo_id+");'>";
			}else{
				var returnStr = "<INPUT align='center' type='button' value='删除' onclick='Delete(&quot;"+cal+"&quot;,"+self+");'>";
			}
			return returnStr;
		};
		var win = Ext.getCmp('scenbtn-win-' + cal);
		if (!win) {
			win = Ext.create('Ext.Window', {
				id : 'scenbtn-win-' + cal,
				width : 800,
				height : '80%',
				title : '场景按钮权限',
				modal : true,
				layout: 'fit',
				items :[{
					xtype : 'panel',
					frame:true,
					autoScroll : true,
					items: [grid.getform(jo_id,self),{
						xtype: 'gridpanel',
						id:'sceneBtn'+cal,
						columnLines : true,
						features: [Ext.create('Ext.grid.feature.Grouping',{
					    	startCollapsed: false,
					        groupHeaderTpl: '{name} ({rows.length})'
					    })],
						columns : [ {
				        	text: '权限',
				        	cls: 'x-grid-header-1',
				            xtype: 'checkcolumn',
				            dataIndex : 'ok_',
				            flex : 0.3,
				            align: 'center'
						},{
							header:'场景按钮',
							cls: 'x-grid-header-1',
							dataIndex : 'sb_title',
							flex : 0.5,
							editor: {
								xtype: 'textfield'
							}
						}, {
							text : 'SBID',
							dataIndex : 'sb_id',
							hidden : true
						}, {
							text : 'sbcaller',
							dataIndex : 'sb_relativecaller',
							hidden : true
						},{
							text : 'caller',
							dataIndex : 'ssp_caller',
							hidden : true
						},  {
							text : 'ID',
							dataIndex : 'ssp_id',
							hidden : true
						}, {
							header:'描述',
							cls: 'x-grid-header-1',
							dataIndex : 'ssp_desc',
							flex : 0.8,
							editor: {
								xtype: 'textfield'
							}
						},{
							header:'链接',
							cls: 'x-grid-header-1',
							dataIndex : 'sb_spaction',
							flex : 1.2,
							dbfind: "SysSpecialPower|ssp_action",
							editor: {
								xtype:"triggerfield",
								triggerCls: 'x-form-search-trigger',
								onTriggerClick: function() {
										var record = Ext.getCmp('sceneBtn'+cal).selModel.lastSelected;
					    				grid.showActions(record);
								}
						    }
						},{
				        	text: '有效',
				        	cls: 'x-grid-header-1',
				            xtype: 'checkcolumn',
				            dataIndex : 'ssp_valid',
				            flex : 0.3,
				            align: 'center'
						},{
							text:'操作',
							cls: 'x-grid-header-1',
							align: 'center',
							dataIndex: 'button',
							flex:0.3,
							renderer:showButton
						}],
						store : new Ext.data.Store({
							fields : [{name:'bs_title',type:'string'},
								{name:'ok_',type:'bool',
									convert:function(value,record){
										if(typeof(record.data['ok_'])!='undefined'&&record&&!record.data['ssp_valid']){
											return true;
										}else{
											return value
										}
									}
								},
								{name:'sb_title',type:'string'},
								{name:'sb_id',type:'number'},
								{name:'sb_relativecaller',type:'string'},
								{name:'sb_alias',type:'string'},
								{name:'ssp_caller',type:'string'},
								{name:'ssp_id',type:'number'},
								{name:'ssp_desc',type:'string'}, 
								{name:'sb_spaction',type:'string'},
								{name:'ssp_valid',type:'bool'},
								{name:'sp_id',type:'number'},
								{name:'groupnum',type:'number'}],
							groupers: [{
					        	property: 'bs_title',
					        	sorterFn : function(com1,com2) {
					        		if(com1.data.groupnum<com2.data.groupnum){
					        			return -1;
					        		}else if(com1.data.groupnum>com2.data.groupnum){
					        			return 1;
					        		}else{
					        			return 0;
					        		}
					        	}
					        }]
						}),
						plugins : [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit : 1
						})]
					}]
				}],
				buttonAlign: 'center',
				buttons: [{
					text: $I18N.common.button.erpSaveButton,
					iconCls: 'x-button-icon-save',
					handler: function(btn) {
						grid.saveSceneBtnPowers(cal, btn.ownerCt.ownerCt.down('gridpanel'),jo_id,self);
					}
				},{
					text: $I18N.common.button.erpCloseButton,
					iconCls: 'x-button-icon-close',
					handler: function(btn) {
						btn.ownerCt.ownerCt.close();
					}
				}]
			});
		}
		win.show();
		this.getSceneBtnPowers(cal, win.down('grid'),jo_id,self);
	},
	getform: function(joid,self){
		var grid = this,ids='',names='';
		if(!joid){
			var id = 'joid';
			var name = 'joname';
			if(self){
				id = 'emid';
				name = 'emname';
				grid.store.each(function(item){
					if(!Ext.isEmpty(item.get('jo_id')) && !Ext.isEmpty(item.get('jo_name'))) {
						ids += ','+item.get('jo_id');
						names += ','+item.get('jo_name');
					}
				});
				ids = ids.substring(1);
				names = names.substring(1);
			}
			return Ext.create('Ext.form.Panel',{
				frame:true,
				layout : 'column',
				items:[{
					fieldLabel: "ID",
					xtype:'textfield',
					id:id,
					name:id,
					value:ids,
					hidden:true
				},{
					fieldLabel: "对象", 
					labelStyle: "color:#FF0000",
					//width:600,
					columnWidth:0.8,
					allowBlank: false,
					id:name,
					name:name,
					value: names,
					xtype:"triggerfield",
					triggerCls: 'x-form-search-trigger',
					onTriggerClick: function() {
						if(self){
							app.getController('ma.Power').showPersonal(grid,true);
						}else{
							grid.showPosition();
						}
					}
				}]
			});
		}else{
			return {};
		}
	},
	showPosition : function() {
		var grid = this;
		var win = Ext.getCmp('job-win');
		if (!win) {
			win = Ext.create('Ext.Window', {
				id : 'job-win',
				width : 800,
				height : 600,
				title : '岗位',
				modal : true,
				closeAction:'hide',
				layout: 'anchor',
				items : [ {
					xtype : 'gridpanel',
					anchor: '100% 100%',
					autoScroller:true,
					columnLines : true,
					plugins : [Ext.create(
							'erp.view.core.grid.HeaderFilter'											
					), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					selModel : Ext.create(
							'Ext.selection.CheckboxModel', {
								checkOnly : true,
								headerWidth : 30
							}),
					columns : [ {
						text : 'ID',
						dataIndex : 'jo_id',
						hidden : true
					}, {
						text : '编号',
						dataIndex : 'jo_code',
						flex : 1,
						filter: {xtype: 'textfield', filterName: 'jo_code'}
					}, {
						text : '岗位名称',
						dataIndex : 'jo_name',
						flex : 1,
						filter: {xtype: 'textfield', filterName: 'jo_name'}
					}, {
						text : '工作描述',
						dataIndex : 'jo_description',
						flex : 1,
						filter: {xtype: 'textfield', filterName: 'jo_description'}
					},{
						text : '组织',
						dataIndex : 'jo_orgname',
						flex : 1,
						filter: {xtype: 'textfield', filterName: 'jo_orgname'}
					} ],
					store:Ext.create('Ext.data.Store',{
						fields : [ {
							name : 'jo_id',
							type : 'number'
						}, 'jo_code', 'jo_name','jo_description','jo_orgname'],
						data:[],
						autoLoad: false
					}),
					listeners: {
						afterrender: function() {
							grid.getData(this);
						},
						scrollershow: function(scroller) {
							if (scroller && scroller.scrollEl) {
								scroller.clearManagedListeners();  
								scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
							}
						}
					}
				}],
				buttonAlign: 'center',
				buttons: [{
					text: $I18N.common.button.erpConfirmButton,
					iconCls: 'x-btn-confirm',
					handler: function(btn) {
						var win = btn.ownerCt.ownerCt;
						grid.setPositions(win.down('gridpanel'));
						win.hide();
					}
				},{
					text: $I18N.common.button.erpCloseButton,
					iconCls: 'x-btn-close',
					handler: function(btn) {
						btn.ownerCt.ownerCt.hide();
					}
				}]
			});
		}
		win.show();
	},
	getData:function(grid){
		Ext.Ajax.request({
        	url : basePath + 'hr/employee/getHrJobs.action',
        	method : 'post',
		    callback : function(opt, s, res){
		       	var r = new Ext.decode(res.responseText);
		       	if(r.exceptionInfo){
		    		showError(r.exceptionInfo);return;
		    	} else if(r.success && r.data){
			    	grid.getStore().loadData(r.data);		    
		    	}
		    }
		});
	},
	setPositions:function(grid){
		var ids = '',names ='';
		var id = Ext.getCmp('joid'),name = Ext.getCmp('joname');
		Ext.each(grid.selModel.getSelection(),function(r){
			if(!Ext.isEmpty(r.get('jo_id')) && r.get('jo_id') > 0) {
				ids += ','+r.get('jo_id');
				names += ','+r.get('jo_name');
			}		
	    });
	    if(ids.length>0&&names.length>0){
	    	id.setValue(ids.substring(1));
	    	name.setValue(names.substring(1));
	    }
	},
	showActions : function(record) {
		var me = this;
		var win = Ext.getCmp('act-win');
		if (!win) {
			win = Ext.create('Ext.Window', {
				id : 'act-win',
				width : 880,
				height : 600,
				title : '对应action',
				modal : true,
				layout: 'anchor',
				items : [ {
					xtype : 'gridpanel',
					anchor: '100% 100%',
					autoScroller:true,
					columnLines : true,
					plugins : [Ext.create(
							'erp.view.core.grid.HeaderFilter'											
					), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					columns : [ {
						header:'<div style="text-align:center">ID</div>',
						dataIndex : 'ssp_id',
						hidden : true
					}, {
						header:'<div style="text-align:center">界面CALLER</div>',
						dataIndex : 'ssp_caller',
						flex : 3,
						filter: {xtype: 'textfield', filterName: 'ssp_caller'}
					}, {
						header:'<div style="text-align:center">对应的ACTION</div>',
						dataIndex : 'ssp_action',
						flex : 6,
						filter: {xtype: 'textfield', filterName: 'ssp_action'}
					}, {
						header:'<div style="text-align:center">权限描述</div>',
						dataIndex : 'ssp_desc',
						flex : 4,
						filter: {xtype: 'textfield', filterName: 'ssp_desc'}
					},{
						header:'是否有效',
						xtype:'yncolumn',
						align: 'center',
						dataIndex : 'ssp_valid',
						flex : 1.5,
						filter: {
							dataIndex:"ssp_valid",
			                xtype:"combo",
			                queryMode:"local",
			                displayField:"display",
			                valueField:"value",
			                store:{
			                    fields:["display","value"],
			                    data:[
			                        {display:"是",value:"-1"},
			                        {display:"否",value:"0"}
			                    ]}
						}
					}],
					store:Ext.create('Ext.data.Store',{
						fields : [ {
							name : 'ssp_id',
							type : 'number'
						}, 'ssp_caller', 'ssp_action','ssp_desc','ssp_valid' ],
						autoLoad: false
					}),
					listeners: {
						afterrender: function() {
							me.getActionsData(this,record);
						},
						scrollershow: function(scroller) {
							if (scroller && scroller.scrollEl) {
								scroller.clearManagedListeners();  
								scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
							}
						},
						itemclick: function(view,recd,item,index){
							record.set('ssp_id',recd.get('ssp_id'));
							record.set('ssp_caller',recd.get('ssp_caller'));
							record.set('sb_spaction',recd.get('ssp_action'));
							record.set('ssp_desc',recd.get('ssp_desc'));
							record.set('ssp_valid',recd.get('ssp_valid')==-1);
							view.ownerCt.ownerCt.close();
						}
					}
				}],
				buttonAlign: 'center',
				buttons: [{
					text: $I18N.common.button.erpCloseButton,
					iconCls: 'x-btn-close',
					handler: function(btn) {
						btn.ownerCt.ownerCt.close();
					}
				}]
			});
		}
		win.show();
	},
	getActionsData:function(grid,record){
		var caller = record.data['sb_relativecaller'];
		var alias = record.data['sb_alias'];
		Ext.Ajax.request({
        	url : basePath + 'bench/ma/getActionsData.action',
        	params : {
	   			caller: caller,
	   			alias: alias
	   		},
		    method : 'post',
		    callback : function(opt, s, res){
		       	var r = new Ext.decode(res.responseText);
		       	if(r.exceptionInfo){
		    		showError(r.exceptionInfo);return;
		    	} else if(r.success && r.data){
		    		grid.getStore().loadData(r.data);		    
		    	}
		    }
		});
	},
	getSceneBtnPowers: function(cal, grid,joid,self) {
		var param = {benchcode: cal};
		if(joid){
			if(self){
				param.emid = joid;
			}else{
				param.joid = joid;
			}
		}
		Ext.Ajax.request({
			url: basePath + 'ma/power/getSceneBtnPowers.action',
			params: param,
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else if(rs.powers){
					if(rs.powers.length==0){
						rs.powers=[ {}, {}, {}, {}, {} ];
					}
					grid.store.loadData(rs.powers);
				}
			}
		});
	},
	saveSceneBtnPowers: function(cal,grid,joid,self) {
		var me = this,param = {benchcode: cal},grouppower = false;
		if(!joid){
			grouppower = true;
			if(self){
				var emid = Ext.getCmp('emid').value;
				if(!Ext.isEmpty(emid)){
					param.emid = emid;
				}
			}else{
				joid = Ext.getCmp('joid').value;
				if(!Ext.isEmpty(joid)){
					param.joid = joid;
				}
			}
		}else{
			if(self){
				param.emid = joid;
			}else{
				param.joid = joid;
			}
		}
		
		var data = new Array();
		grid.store.each(function(item){
			if(item.dirty||grouppower) {
				if(!Ext.isEmpty(item.get('ssp_desc')) && !Ext.isEmpty(item.get('sb_spaction'))) {
					item.data.ssp_valid = item.data.ssp_valid==true?"-1":"0";
					data.push(item.data);
				}
			}
		});
		if(data.length > 0) {
			param.data = unescape(Ext.encode(data).replace(/\\/g,"%"));
			Ext.Ajax.request({
				url: basePath + 'ma/power/saveSceneBtnPowers.action',
				params: param,
				callback: function(opt, s, r) {
					var rs = Ext.decode(r.responseText);
					if(rs.exceptionInfo) {
						showError(rs.exceptionInfo);
					} else if(rs.success){
						showMessage('提示','保存成功！');
						if(!grouppower){
							me.getSceneBtnPowers(cal, grid,joid,self);
						}else{
							me.getSceneBtnPowers(cal, grid);
						}
					}
				}
			});
		}else{
			showError("请填写描述和链接！");
		}
	},
    tbar: [{
    	xtype: 'tbtext',
    	id: 'power_title'
    },'->',{
        xtype: 'checkbox',
        boxLabel:'按角色授权',
        name:'power_role',
    	id: 'power_role',
    	hidden:!defaultPowerSetting,
    	value:_role?1:0,
    	checked:_role,
    	margin: '0 5 0 0'
    },{
    	xtype: 'combo',
    	fieldLabel: '搜索角色',
    	margin: '0 5 0 0',
    	width: 300,
    	labelWidth: 80,
    	id: 'role_combo',
    	hidden:!_role,
    	displayField: 'display',
    	valueField: 'value',
    	queryMode: 'local',
    	anyMatch: true,
    	store: new Ext.data.Store({
    		fields: ['display', 'value']
    	}),
    	listeners: {
    		change: function(c) {
    			var v = c.value, grid = c.ownerCt.ownerCt;
    			if(!Ext.isEmpty(v)) {
    				grid.store.clearFilter(true);
    				if(v != ' ') {
    					grid.store.filter(new Ext.util.Filter({
        				    filterFn: function(item) {
        				        return item.get('ro_name') == v;
        				    }
        				}));
    				} else {
    					grid.store.filter(new Ext.util.Filter({
        				    filterFn: function(item) {
        				        return true;
        				    }
        				}));
    				}
    			}
    		}
    	}
    },{
    	xtype: 'combo',
    	fieldLabel: '搜索岗位',
    	margin: '0 5 0 0',
    	width: 300,
    	labelWidth: 80,
    	id: 'job_combo',
    	hidden:_role,
    	displayField: 'display',
    	valueField: 'value',
    	queryMode: 'local',
    	anyMatch: true,
    	store: new Ext.data.Store({
    		fields: ['display', 'value']
    	}),
    	listeners: {
    		change: function(c) {
    			var v = c.value, grid = c.ownerCt.ownerCt;
    			if(!Ext.isEmpty(v)) {
    				grid.store.clearFilter(true);
    				if(v != ' ') {
    					grid.store.filter(new Ext.util.Filter({
        				    filterFn: function(item) {
        				        return item.get('jo_name') == v;
        				    }
        				}));
    				} else {
    					grid.store.filter(new Ext.util.Filter({
        				    filterFn: function(item) {
        				        return true;
        				    }
        				}));
    				}
    			}
    		}
    	}
    },{
    	text: '个人权限',
    	cls: 'x-btn-blue',
    	margin: '0 5 0 0',
    	id: 'personal_set',
    	disabled:_role
    },{
    	text: '特殊权限设置',
    	cls: 'x-btn-blue',
    	margin: '0 5 0 0',
    	id: 'special_set'
    },{
    	text: '复制权限',
    	cls: 'x-btn-blue',
    	margin: '0 5 0 0',
    	id: 'power_copy',
    	disabled:_role
    },{
    	text: '权限同步',
    	cls: 'x-btn-blue',
    	margin: '0 5 0 0',
    	id: 'power_sync'
    },{
        text: '权限全部同步',
        cls: 'x-btn-blue',
        iconCls : '',
        width : 100,
        hidden:true,
        xtype: 'erpSyncButton',
        margin: '0 5 0 0',
        id: 'power_cover'
    },{
    	iconCls: 'tree-save',
    	cls: 'x-btn-blue',
		text: $I18N.common.button.erpSaveButton,
		handler: function(){
			var me = this.ownerCt.ownerCt;
			var grid = Ext.getCmp('grid'),set = grid.powerSet;
			var type = grid.urlType;
			console.log(type);
			if(type=='bench'){
				return ;
			}
			if(_role){
				me.saveRolePower(grid,set,type);
			}else{
				me.saveJobOrPersonalPower(grid,set,type);
			}
		}
    }, '->'],
    saveRolePower:function(grid,set,type){
    	var rolePowers = new Array();
    	var rolePower = null;
    	Ext.each(grid.store.data.items, function(d){
			if(this.dirty){
				rolePower = new Object();
				rolePower.pp_caller = grid.pp_caller;
				rolePower.rp_id = d.data['rp_id'];
				if(grid._self) {
					rolePower.pp_emid = d.data['ro_id'];
				} else {
					rolePower.rp_roid = d.data['ro_id'];
				}
				
				Ext.each(set, function(s){
					rolePower[s] = d.data[s] ? 1 : 0;
				});
				rolePowers.push(rolePower);
			}
		});
		
		if(rolePowers.length > 0){
			grid.setLoading(true);
			Ext.Ajax.request({//拿到grid的columns
	        	url : basePath + 'hr/employee/updateRolePower.action',
	        	params: {
	        		update: Ext.encode(rolePowers),
	        		_self: grid._self,
	        		utype: type
	        	},
	        	method : 'post',
	        	timeout : 240000,
	        	callback : function(options,success,response){
	        		grid.setLoading(false);
	        		var res = new Ext.decode(response.responseText);
	        		if(res.exception || res.exceptionInfo){
	        			showError(res.exceptionInfo);
	        			return;
	        		}
	        		if(res.success){
	        			updateSuccess(function(){
	        				if(grid._self) {
	        					grid.getPersonalData(grid.urlType, grid._persons);
	        				}else if(_role){
	        					grid.getRolePowerData(grid.urlType);
	        				}else {
	        					grid.getGroupData(grid.urlType);
	        				}
	        			});
	        		}
	        	}
			});
		}
    },
    saveJobOrPersonalPower:function(grid,set,type){
    	var positionpowers = [];
		var positionpower = null;
		Ext.each(grid.store.data.items, function(d){
			if(this.dirty){
				positionpower = new Object();
				positionpower.pp_caller = grid.pp_caller;
				positionpower.pp_id = d.data['pp_id'];
				if(grid._self) {
					positionpower.pp_emid = d.data['jo_id'];
				} else {
					positionpower.pp_joid = d.data['jo_id'];
				}
				
				Ext.each(set, function(s){
					positionpower[s] = d.data[s] ? 1 : 0;
				});
				positionpowers.push(positionpower);
			}
		});
		if(positionpowers.length > 0){
			grid.setLoading(true);
			Ext.Ajax.request({//拿到grid的columns
	        	url : basePath + 'hr/employee/updateJobPower.action',
	        	params: {
	        		update: Ext.encode(positionpowers),
	        		_self: grid._self,
	        		utype: type
	        	},
	        	method : 'post',
	        	timeout : 240000,
	        	callback : function(options,success,response){
	        		grid.setLoading(false);
	        		var res = new Ext.decode(response.responseText);
	        		if(res.exception || res.exceptionInfo){
	        			showError(res.exceptionInfo);
	        			return;
	        		}
	        		if(res.success){
	        			updateSuccess(function(){
	        				if(grid._self) {
	        					grid.getPersonalData(grid.urlType, grid._persons);
	        				} else {
	        					grid.getGroupData(grid.urlType);
	        				}
	        			});
	        		}
	        	}
			});
		}
    },
	initComponent : function(){ 
		if(defaultHrJobPowerExists){
			this.powerSet.push('pp_jobemployee');
			this.listSet.push('pp_jobemployee');
		}
		
		this.defaultColumns = this.jobColumns.concat(this.basicPowerColumns);
		this.listColumns = this.jobColumns.concat(this.basicListColumns);
		this.dealColumns = this.jobColumns.concat(this.basicDealColumns);
		this.roleDefaultColumns = this.roleColumns.concat(this.basicPowerColumns)
		this.roleListColumns = this.roleColumns.concat(this.basicListColumns);
		this.roleDealColumns = this.roleColumns.concat(this.basicDealColumns);
		
		if(_role){
			this.columns = this.roleDefaultColumns;
		}else{
			this.columns = this.defaultColumns;
		}
		jo_name = getUrlParam('jo_name');
		if(jo_name!=''){
			Ext.apply(this,{
				 headerFilters:{'jo_name':jo_name}
		    });
		}
		this.callParent(arguments);
	},
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	},
	powerSet: ['pp_see', 'pp_selflist', 'pp_alllist', 'pp_add', 'pp_delete', 'pp_save', 'pp_saveoth', 'pp_commit',
	           'pp_uncommit', 'pp_audit', 'pp_unaudit', 'pp_print', 'pp_printoth', 'pp_printrepeat', 'pp_disable', 
	           'pp_undisable',  'pp_closed', 'pp_unclosed', 'pp_posting',  'pp_unposting'],
	listSet:['pp_selflist','pp_alllist'],
	dealSet:['pp_see'],
    defaultSet:['pp_add','pp_see', 'pp_delete', 'pp_save','pp_saveoth','pp_commit','pp_uncommit','pp_audit','pp_unaudit','pp_print','pp_printoth','pp_printrepeat','pp_disable', 'pp_undisable', 'pp_closed', 'pp_unclosed', 'pp_posting', 'pp_unposting'],
    getRolePowerData:function(type,roleSelected){
 		var me = this;
		me.setLoading(true);
		Ext.Ajax.request({
        	url : basePath + 'hr/employee/getHrRole.action',
        	params: {
        		caller: me.pp_caller,
        		utype: type,
        		_self: false
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		me.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}
        		if(!res.role){
        			return;
        		} else {
        			me.formdetail = res.formdetail;
        			me.detailgrid = res.detailgrid;
        			me.relativeSearch=res.relativeSearch;
        			me.datalist = res.datalist;
        			me.title = res.title;
        			me.callers = res.callers;
        			me.relativedatalist = res.relativedatalist;
        			
        			//var me = this;
					var data = new Array();
					var columns = me.roleColumns.concat(me.basicPowerColumns);
					Ext.each(res.role, function(role){
						Ext.each(res.rolepower, function(rolepower){
							if(role.ro_id == rolepower.rp_roid){
								role = Ext.Object.merge(role, rolepower);
								role.rp_id = rolepower.rp_id;
								Ext.each(me.powerSet, function(s){ 
									role[s] = rolepower[s] == 1;
								});
							}
						});
						data.push(role);
					});	
        			
        			me.store.loadData(data);
        			
        			Ext.getCmp('special_set').setText('特殊权限设置');
        			var bol = (me.urlType != type)||roleSelected; //判断是否需要reconfigure
        			if(type == 'list' && bol){ //从非列表界面切换到列表界面
        				me.reconfigure(me.store, me.roleListColumns);
        			}else if(type == 'deal' && bol){ //从非批处理界面切换到批处理界面
        				me.reconfigure(me.store, me.roleDealColumns);
        			}else if(type == null && bol){ //从非维护界面切换到维护界面
        				me.reconfigure(me.store, me.roleDefaultColumns);
        			}else{ //同类型页面切换
        				me.store.loadData(data);
        			}
        			
        			me.urlType = type;
        			me._self = false;
        			me.filterRole(res.role);
        			me.resetHeaderChecker();
        			me.rememberLastFilter();
        			me.plugins[0].renderFilters();
        		}
        	}
        });   	
    },
	getGroupData: function(type,roleSelected){
		var me = this;
		me.setLoading(true);
		Ext.Ajax.request({
        	url : basePath + 'hr/employee/getHrJob.action',
        	params: {
        		caller: me.pp_caller,
        		utype: type,
        		_self: false
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		me.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}
        		if(!res.hrjob){
        			return;
        		} else {
        			var data = new Array();
        			Ext.each(res.hrjob, function(){
        				var j = this;
        				Ext.each(res.positionpower, function(pp){
        					if(j.jo_id == pp.pp_joid){
        						j = Ext.Object.merge(j, pp);
        						j.pp_id = pp.pp_id;
    							Ext.each(me.powerSet, function(s){
        							j[s] = pp[s] == 1;
        						});
    						}
        				});
        				data.push(j);
        			});
        			me.formdetail = res.formdetail;
        			me.detailgrid = res.detailgrid;
        			me.relativeSearch=res.relativeSearch;
        			me.datalist = res.datalist;
        			me.title = res.title;
        			me.callers = res.callers;
        			me.relativedatalist = res.relativedatalist;
        			
        			if(type == 'bench' && me.urlType != 'bench'){
        				Ext.getCmp('special_set').setText('场景按钮权限');
        				me.Store = me.store;
        				var store = Ext.create('Ext.data.Store', {
					    	fields: [{
					    		name: 'em_id',
					    		type: 'number'
					    	},{
					        	name: 'jo_orgid',
					        	type: 'number'
					        },{
					        	name:'jo_orgname',
					        	type:'string'
					        },{
					        	name:'jo_description',
					        	type:'string'
					        },{
					        	name:'jo_name',
					        	type:'string'
					        },{
					        	name:'jo_id',
					        	type:'number'
					        },{
					        	name:'bp_id',
					        	type:'number'
					        },{
					        	name:'bp_view',
					        	type:'bool'
					        }],
					        data:data,
					        sorters: [{
					            property : 'jo_id',
					            direction: 'ASC'
					        }],
					        groupField:'jo_orgname'
					    });
					    me.reconfigure(store, me.benchColumns);
        			}

        			me._self = false;
        			if(type != 'bench'&&me.urlType == 'bench'){
        				me.store = me.Store;
        				Ext.getCmp('special_set').setText('特殊权限设置');
        			}
        			me.store.loadData(data);
        			
        			var bol = (me.urlType != type)||roleSelected; //判断是否需要reconfigure
        			if(type == 'list' && bol){ //从非列表界面切换到列表界面
        				me.reconfigure(me.store, me.listColumns);
        			}else if(type == 'deal' && bol){ //从非批处理界面切换到批处理界面
        				me.reconfigure(me.store, me.dealColumns);
        			}else if(type == null && bol){ //从非维护界面切换到维护界面
        				me.reconfigure(me.store, me.defaultColumns);
        			}
        			
        			//先reconfigure再setText
        			var cm = me.down('gridcolumn[dataIndex=jo_name]');
        			if(cm){
        				cm.setText('岗位名称');
        			}
        			
        			me.urlType = type;
        			me.filterJob(res.hrjob);
        			me.resetHeaderChecker();
        			me.rememberLastFilter();
        			me.plugins[0].renderFilters();
        		}
        	}
        });
	},
	rememberLastFilter: function() {
		var me = this, filter = this.store.filters.findBy(function(filter){
            return filter.property == '$all';
        });
		if(filter && filter.filterArr) {
			me.headerFilters = {};
			Ext.Array.each(filter.filterArr, function(f){
				me.headerFilters[f.property] = f.value;
			});
		}
	},
	resetHeaderChecker: function() {
		var columns = this.headerCt.getGridColumns();
		Ext.Array.each(columns, function(c){
			if(c.headerCheckable) {
				var ch = Ext.get(c.dataIndex + '-checkbox');
				ch && (ch.dom.checked = false);
			}
		});
	},
	selModel: Ext.create('Ext.selection.CheckboxModel',{
		checkOnly:true,
		listeners:{
			 'select': function(selModel, record){
				 selModel.view.ownerCt.selectAllPower(record);
			 },
			 'deselect': function(selModel, record){
				 selModel.view.ownerCt.deselectAllPower(record);
			 }
		},
		onHeaderClick: function(headerCt, header, e) {
			var grid = headerCt.ownerCt;
			if (header.isCheckerHd) {
				var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
				if (!isChecked) {
					grid.store.each(function(item){
						grid.selectAllPower(item);
					});
					grid.selModel.selectAll();
				} else {
		        	grid.store.each(function(item){
						grid.deselectAllPower(item);
					});
		        	grid.selModel.deselectAll();
		        }
	        } 
			
		},
	    getHeaderConfig: function() {
	        var me = this;

	        return {
	            isCheckerHd: false,
	            disabled:true,
	            text : '&#160;',
	            width: me.headerWidth,
	            sortable: false,
	            draggable: false,
	            resizable: false,
	            hideable: false,
	            menuDisabled: true,
	            dataIndex: '',
	           // cls: Ext.baseCSSPrefix + 'column-header-checkbox ',
	            renderer: Ext.Function.bind(me.renderer, me),
	            locked: me.hasLockedHeader()
	        };
	    }

	}),
	selectAllPower: function(record){
		var me = this, set = null;
		if(me.urlType=='list'){
		  set=me.listSet;
		}else if(me.urlType=='deal'){
			set=me.dealSet;
		}else set=me.defaultSet;
		Ext.each(set, function(s){			
			record.set(s, true);
		});
	},
	deselectAllPower: function(record){
		var me = this, set = null;
		if(me.urlType=='list'){
		  set=me.listSet;
		}else if(me.urlType=='deal'){
			set=me.dealSet;
		}else set=me.defaultSet;
		Ext.each(set, function(s){			
			record.set(s, false);
		});
	},
	setMore: function(caller, id,roleId){
		var me = this;
		var url = null;
		var requestId;
		if(!_role){
			url = basePath + 'hr/employee/getHrJobLimits.action';
			requestId = id;
		}else{
			url = basePath + 'hr/employee/getHrRoleLimits.action';
			requestId = roleId;
		}
		
		var winId = 'win_' + caller + '_' + requestId + '_' + (me.urlType || '') + '_' + me._self + '_' + _role,
			win = Ext.getCmp(winId);
		
		if(win) {
			win.show();
		} else {
			var limitsArr=[],relativelimitsArr=[];
			Ext.Ajax.request({
				url: url,
				async:false,
				params: {
					caller: caller,
					utype: me.urlType,
					_self: me._self,
					id: requestId
				},
				method: 'post',
				callback: function(opt, s, r) {
					var res = Ext.decode(r.responseText);
					if(res.limits) {
						limitsArr=res.limits;
					}
					if(res.relativelimits){
						relativelimitsArr=res.relativelimits;
					}
				}
			});				
			var f = me.formdetail,g = me.detailgrid,ti = me.title,rs=me.relativeSearch,d = me.datalist,r=me.relativedatalist, mainitems = new Array(),relativeitems=new Array(),mainanchor='';
			var winItem=new Array();
			if(d && d.length > 0) {//列表
				Ext.each(d, function(i){
					if(i.dld_width > 0) {
						var checked=false;
						Ext.each(limitsArr,function(l){//limitsArr[0],function(l){
							if(l.lf_isform==2&&l.lf_field==i.dld_field){
								checked=true;
								return false;
							}							
						});
						mainitems.push({
							xtype: 'checkbox',
							name: i.dld_field,
							boxLabel: i.dld_caption,
							checked:checked
						});
					}
				});
				if(r && r.length>0) me.relativeCaller=r[0].dld_caller;
				Ext.each(r, function(i){
					if(i.dld_width > 0) {
						var checked=false;
						Ext.each(relativelimitsArr,function(l){
							if(l.lf_isform==2&&l.lf_field==i.dld_field){
								checked=true;
								return false;
							}							
						});
						relativeitems.push({
							xtype: 'checkbox',
							name: i.dld_field,
							boxLabel: i.dld_caption,
							checked:checked
						});
					}
				});
				mainanchor=relativeitems.length>0?'100% 50%':'100% 100%';  
				winItem.push({
					anchor: mainanchor,
					xtype: 'form',
					group: 'list',
					bodyStyle: 'background:#f1f1f1;',
					autoScroll : true,
					items: mainitems,
					layout: {
						type: 'table',
						columns: 7
					},
					fieldDefaults: {
						margin: '2 10 2 10',
						checkedCls: 'checked'
					}
				});
				if(relativeitems.length>0){
					winItem.push({
						anchor: '100% 50%',
						xtype: 'form',
						title:'<h2>关联列表<h2>',
						group: 'relativelist',
						bodyStyle: 'background:#f1f1f1;',
						autoScroll : true,
						layout: {
							type: 'table',
							columns: 7
						},
						items:relativeitems,
						fieldDefaults: {
							margin: '2 10 2 10',
							checkedCls: 'checked'
						}
					});
				}
			}else{
				var formpanel_f={}, formpanel_g={},relativeSearchPanel={};
				if(f && f.length > 0) {//form
					var items = new Array();
					Ext.each(f, function(i){
						if(i.fd_type != 'H') {
							var checked=false;
							Ext.each(limitsArr,function(l){
								if(l.lf_isform==1&&l.lf_field==i.fd_field){
									checked=true;
									return false;
								}							
							});
							items.push({
								xtype: 'checkbox',
								name: i.fd_field,
								boxLabel: i.fd_caption,
								checked:checked
							});
						}
					});
					var h = g[0].length == 0 ? 100 : 50;
					formpanel_f={
							anchor: '100% ' + h + '%',
							xtype: 'form',
							group: 'form',
							bodyStyle: 'background:#f1f1f1;',
							autoScroll : true,
							items: items,
							layout: {
								type: 'table',
								columns: 7
							},
							fieldDefaults: {
								margin: '2 10 2 10',
								checkedCls: 'checked'
							}
						};
				}
				if(g && g.length > 0) {
					if(g.length>1){
							var itemss = new Array();
							for(var j=0;j<g.length;j++){
								var items = new Array();
								var gridcaller='';
								Ext.each(g[j], function(i){
									gridcaller=i.dg_caller;
									if(i.dg_width > 0) {
										var checked=false;
										Ext.each(limitsArr,function(l){
											if(l.lf_isform==0&&l.lf_field==i.dg_field){
												checked=true;
												return false;
											}							
										});
										items.push({
											xtype: 'checkbox',
											name: i.dg_field,
											boxLabel: i.dg_caption,
											checked:checked
										});
									}
								});
								var tt= {
										xtype: 'form',
										group: 'grid',
										bodyStyle: 'background:#f1f1f1;',
										autoScroll : true,
										caller:gridcaller,
										title:ti[j],
										items: items,
										layout: {
											type: 'table',
											columns: 7
										},
										fieldDefaults: {
											margin: '2 10 2 10',
											checkedCls: 'checked'
										}
									};
								itemss.push(tt);
							}
							var h = f.length == 0 ? 100 : 50;
							formpanel_g={
									xtype:'tabpanel',
									anchor: '100% ' + h + '%',
									items:[itemss]							
								};
					}else if(g[0].length>0){
						var gridcaller='';
						var items = new Array();
						Ext.each(g[0], function(i){
							gridcaller=i.dg_caller;
							if(i.dg_width > 0) {
								var checked=false;
								Ext.each(limitsArr,function(l){
									if(l.lf_isform==0&&l.lf_field==i.dg_field){
										checked=true;
										return false;
									}							
								});
								items.push({
									xtype: 'checkbox',
									name: i.dg_field,
									boxLabel: i.dg_caption,
									checked:checked
								});
							}
						});
						var h = f.length == 0 ? 100 : 50;
						formpanel_g={
									anchor: '100% ' + h + '%',
									xtype: 'form',
									group: 'grid',
									caller:gridcaller,
									bodyStyle: 'background:#f1f1f1;',
									autoScroll : true,
									items: items,
									layout: {
										type: 'table',
										columns: 7
									},
									fieldDefaults: {
										margin: '2 10 2 10',
										checkedCls: 'checked'
									}
							};
					}
					
					
				}			
				if(rs && rs.length > 0) {//关联查询	
					var arr=new Array();
					for(var i=0;i<rs.length;i++){
						var t=rs[i].form.title;
						var items = new Array();
						Ext.each(rs[i].grid.gridColumns, function(c){
							if(c.xtype != 'H') {
								var checked=false;
								Ext.each(limitsArr,function(l){
									if(l.lf_isform==3&&l.lf_field==c.dataIndex&&l.lf_caller.split('|')[1]==t){
										checked=true;
										return false;
									}							
								});
								items.push({
									xtype: 'checkbox',
									name: c.dataIndex,
									boxLabel: c.text,
									belong:t,
									checked:checked
								});
							}
						});
						arr.push({
							xtype: 'fieldset',
							title:t,
							collapsed:i!=0,
							collapsible: true,
							layout: {
								type: 'table',
								columns: 7
							},
							items:items,
							defaults: {
								margin: '2 10 2 10',
								checkedCls: 'checked'
							}
						});
					}
					relativeSearchPanel={
							xtype: 'form',
							anchor: '100% 100%',
							group:'relativeSearch',
							bodyStyle: 'background:#f1f1f1;',
							autoScroll : true,
							items:arr
					};
				}
				if(me.relativeSearch.length==0){
					winItem.push(formpanel_f);
					winItem.push(formpanel_g);
				}else{
					winItem.push({
						xtype:'tabpanel',
						anchor: '100% 100%',
						items:[{
							title:'维护界面',
							layout: 'anchor',
							items:[formpanel_f,formpanel_g]
						},{
							title:'关联查询',
							layout: 'anchor',
							items:[relativeSearchPanel]
						}]
					});
				}
			}			
			win = Ext.create('Ext.Window', { 
				id: winId,
				title: me.down('#power_title').el.dom.innerHTML + '<center>请选择不允许查看和修改的字段</center>',
				modal: true,
				autoShow: true,
				closeAction:defaultPowerSetting?'destroy':'hide',
				//closeAction: 'hide',
				height: '80%',
				width: '80%',
				layout: 'anchor',
				jobId: id,
				roleId:roleId,
				items:winItem,
				buttonAlign: 'center',
				buttons: [{
					text: $I18N.common.button.erpSaveButton,
					iconCls: 'x-button-icon-save',
			    	cls: 'x-btn-gray',
			    	handler: function(btn){
			    		me.saveMore(btn.ownerCt.ownerCt);
			    	}
				},{
					text: $I18N.common.button.erpCloseButton,
					iconCls: 'x-button-icon-close',
			    	cls: 'x-btn-gray',
			    	handler: function(btn) {
			    		btn.ownerCt.ownerCt.close();
			    	}
				}]
			});
			
		}
	},
	saveMore: function(win){
		var me = this, f = win.down('form[group=form]'), g = win.down('form[group=grid]'),rs=win.down('form[group=relativeSearch]'), 
			d = win.down('form[group=list]'),r=win.down('form[group=relativelist]'), data = new Array(),  
			caller = me.pp_caller, joid = win.jobId,roleId = win.roleId, obj = new Object();
		var grid_ =Ext.ComponentQuery.query('form[group = grid]');// 取多个从表的form 
		var idKey,idValue;
		if(me._self){
			idKey = 'lf_emid';
			idValue = joid;
		}else if(_role){
			idKey = 'lf_roid';
			idValue = roleId;
		}else{
			idKey = 'lf_joid';
			idValue = joid;
		}
		if(f) {
			var items = f.query('checkbox[checked=true]');
			Ext.each(items, function(item){
				obj = new Object();
				obj[idKey] = idValue;
				obj.lf_caller = caller;
				obj.lf_field = item.name;
				obj.lf_isform = 1;
				data.push(obj);
			});
		}
		if(grid_.length>1){  //因为g变成了二维数组，一个从表和没有从表长度都为0，所以下面加了一个判断
			Ext.each(grid_,function(grid){
				var items = grid.query('checkbox[checked=true]');
				Ext.each(items, function(item){
					obj = new Object();
					obj[idKey] = idValue;
					obj.lf_caller = grid.caller;
					obj.lf_field = item.name;
					obj.lf_isform = 0;
					data.push(obj);
				});
			});
		}else if(g){
			var items = g.query('checkbox[checked=true]');
			Ext.each(items, function(item){
				obj = new Object();
				obj[idKey] = idValue;
				obj.lf_caller = caller;
				obj.lf_field = item.name;
				obj.lf_isform = 0;
				data.push(obj);
			});
		}
		if(d) {
			var items = d.query('checkbox[checked=true]');
			Ext.each(items, function(item){
				obj = new Object();
				obj[idKey] = idValue;
				obj.lf_caller = caller;
				obj.lf_field = item.name;
				obj.lf_isform = 2;
				data.push(obj);
			});
		}
		if(r) {
			var items = r.query('checkbox[checked=true]');
			Ext.each(items, function(item){
				obj = new Object();
				obj[idKey] = idValue;
				obj.lf_caller = me.relativeCaller;
				obj.lf_field = item.name;
				obj.lf_isform = 2;
				data.push(obj);
			});
		}
		if(rs) {
			var items = rs.query('checkbox[checked=true]');
			Ext.each(items, function(item){
				obj = new Object();
				obj[idKey] = idValue;
				obj.lf_caller =caller+'|'+item.belong;
				obj.lf_field = item.name;
				obj.lf_isform = 3;
				data.push(obj);
			});
		}
		var islist=me.urlType=='list';
		var saveUrl = basePath + 'hr/employee/saveHrJobLimits.action';
		if(_role){
			saveUrl = basePath + 'hr/employee/saveHrRoleLimits.action';
		}
		me.setLoading(true);
		Ext.Ajax.request({
			url: saveUrl,
			params: {
				caller: caller,
				relativeCaller:me.relativeCaller,
				id: idValue,
				_self: me._self,
				data: Ext.encode(data),
				islist:islist
			},
			method: 'post',
			callback: function(opt, s, r) {
				me.setLoading(false);
				var res = Ext.decode(r.responseText);
				if(res.success) {
					alert('修改成功!');
					win.close();
				}
			}
		});
	},
	getPersonalData: function(type, em){
		var me = this;
		me.setLoading(true);
		me._persons = em;
		var emid = Ext.Array.concate(em, ',', 'em_id');
		Ext.Ajax.request({
        	url : basePath + 'hr/employee/getSelfPower.action',
        	params: {
        		caller: me.pp_caller,
        		utype: type,
        		emid: emid
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		me.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}
        		if(!res.success){
        			return;
        		} else {
        			var data = new Array();
        			Ext.each(em, function(j){
        				var o = new Object();
        				o.jo_id = j.em_id;
        				o.pp_joid = j.em_id;
						o.jo_name = j.em_name;
        				Ext.each(res.personalpower, function(pp){
        					if(j.em_id == pp.pp_emid){
        						o = Ext.Object.merge(o, pp);
        						Ext.each(me.powerSet, function(s){
        							o[s] = pp[s] == 1;
        						});
        					}
        				});
        				data.push(o);
        			});
        			me.formdetail = res.formdetail;
        			me.detailgrid = res.detailgrid;
        			me.datalist = res.datalist!=null?res.datalist.datalist:null;
        			me.relativedatalist = res.datalist!=null?res.datalist.relativedatalist:null;
        			me.store.loadData(data);
        			var cm = me.down('gridcolumn[dataIndex=jo_name]');
        			if(cm){
        				cm.setText('人员名称');
        			}
        			me._self = true;
        			if(type != null) {
        				if(type == 'list' && me.urlType != 'list') {
        					me.reconfigure(me.store, me.listColumns);
        				} else if(type == 'deal' && me.urlType != 'deal') {
        					me.reconfigure(me.store, me.dealColumns);
        				}
        			} else {
        				if(me.urlType != null) {
        					me.reconfigure(me.store, me.defaultColumns);
        				}
        			}
        			me.urlType = type;
        		}
        	}
        });
	},
	setSpecial: function(caller, jo_id,ro_id){
		var id = jo_id;
		if(_role){
			id = ro_id;
		}
		var me = this,
			winId = 'special_win_' + caller + '_' + id + '_' + (me.urlType || '') + '_' + me._self + '_' + _role,
			win = Ext.getCmp(winId);
		if(win) {
			win.show();
		} else {
			win = Ext.create('Ext.Window', {
				id: winId,
				title: me.down('#power_title').el.dom.innerHTML,
				closeAction:defaultPowerSetting?'destroy':'hide',
				modal: true,
				autoShow: true,
				//closeAction: 'hide',
				height: 500,
				width: 360,
				layout: 'anchor',
				jobId: jo_id,
				roleId: ro_id,
				items: [{
					xtype: 'form',
					anchor: '100% 100%',
					autoScroll : true,
					bodyStyle: 'background:#f1f2f5;',
					defaults: {
						margin: '5 5 5 10'
					},
					items: [{
						xtype: 'fieldset',
						title: '特殊功能权限',
						defaults: {
							xtype: 'checkbox',
							margin: '5 5 5 10'
						}
					},{
						xtype: 'fieldset',
						title: '选择不允许查看的关联查询',
						defaults: {
							xtype: 'checkbox',
							margin: '5 5 5 10'
						}
					}]
				}],
				buttonAlign: 'center',
				buttons: [{
					text: $I18N.common.button.erpSaveButton,
					iconCls: 'x-button-icon-save',
			    	cls: 'x-btn-gray',
			    	handler: function(btn){
			    		me.saveSpecial(btn.ownerCt.ownerCt, caller);
			    	}
				},{
					text: $I18N.common.button.erpCloseButton,
					iconCls: 'x-button-icon-close',
			    	cls: 'x-btn-gray',
			    	handler: function(btn) {
			    		btn.ownerCt.ownerCt.close();
			    	}
				}]
			});
			me.getSpecial(caller, win.down('form'), id);
		}
	},
	getSpecial: function(caller, form, id){
		var me = this;
		var url = basePath + 'ma/power/getSysSpecialPowers.action';
		var params = new Object();
		params.caller = caller;
		if(_role){
			url = basePath + 'ma/power/getSysSpecialPowersByRole.action';
			params.ro_id = id;
		}else{
			params.em_id = (me._self ? id : null);
			params.jo_id = (me._self ? null : id);
		}
		Ext.Ajax.request({
			url: url,
			params: params,
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else if(rs.data){
					var f1 = form.items.items[0], f2 = form.items.items[1],
						allSpecials = rs.data, authedSpecials = rs.specialPower,
						allRelativeLimit = rs.relativeSearch, authedRelativeLimit = rs.relativeLimit;
					var _contains = function(arr, key, value) {
						var b = false;
						for (var i in arr) {
							if(arr[i][key] == value) {
								b = true;
								break;
							}
						}
						return b;
					};
					Ext.each(allSpecials, function() {
						f1.add({
							itemId: this.ssp_id,
							itemType: 'specialpower',
							boxLabel: this.ssp_desc,
							checked: _contains(authedSpecials, 'sp_sspid', this.ssp_id)
						});
					});
					if(allRelativeLimit) {
						Ext.each(allRelativeLimit, function() {
							f2.add({
								itemId: this.rs_id,
								itemType: 'relativesearch',
								boxLabel: this.rs_title,
								checked: _contains(authedRelativeLimit, 'rsl_title', this.rs_title)
							});
						});
					} else {
						f2.hide();
					}
					
					form.setAutoScroll(true);
				}
			}
		});
	},
	saveSpecial: function(win, caller) {
		var me = this, form = win.down('form');
		var url = basePath + 'hr/employee/saveSpecialPower.action';
		var id = win.jobId;
		if(_role){
			url = basePath + 'hr/employee/saveRoleSpecialPower.action';
			id = win.roleId;
		}
		if(form.items.items.length > 0) {
			var specials = [], limits = [];
			form.getForm().getFields().each(function (f){
				if(f.itemType == 'specialpower') {
					specials.push({
						sp_sspid: Number(f.itemId),
						checked: f.getValue()
					});
				} else {
					limits.push({
						rsl_caller: caller,
						rsl_title: f.boxLabel,
						checked: f.getValue()
					});
				}
			});
			me.setLoading(true);
			Ext.Ajax.request({
				url: url,
				params: {
					caller: caller,
					id: id,
					_self: me._self,
					specials: Ext.encode(specials),
					limits: Ext.encode(limits)
				},
				callback: function(opt, s, r) {
					me.setLoading(false);
					var res = Ext.decode(r.responseText);
					if(res.success) {
						alert('设置成功!');
						win.close();
					}
				}
			});
		}
		win.hide();
	},
	filterJob: function(jobs) {
		var c = this.down('#job_combo');
		this.jobs = jobs;
		this.filterStoreByCombo(c,jobs,'jo_name');
	},
	filterRole: function(roles) {
		var c = this.down('#role_combo');
		this.roles = roles;
		this.filterStoreByCombo(c,roles,'ro_name');
	},
	filterStoreByCombo:function(combo,datas,dataIndex){
		if(!combo.store || combo.store.data.length == 0) {
			var ns = Ext.Array.pluck(datas, dataIndex), data = new Array(), fd = new Array();
			data.push({display: '--所有--', value: ' '});
			Ext.each(ns, function(){
				if(!Ext.Array.contains(fd, String(this))) {
					data.push({display: this, value: this});
					fd.push(String(this));
				}
			});
			combo.store.loadData(data);
		}
		var v = combo.value;
		
		if(!Ext.isEmpty(v)) {
			this.store.clearFilter(true);
			if(v != ' ') {
				this.store.filter(new Ext.util.Filter({
				    filterFn: function(item) {
				        return item.get(dataIndex) == v;
				    }
				}));
			}else{
				this.store.filter(new Ext.util.Filter({
				    filterFn: function(item) {
				        return true;
				    }
				}));
			}
		}
	}
});