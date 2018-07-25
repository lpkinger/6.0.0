Ext.define('erp.view.fa.ars.APBill',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	GridUtil: Ext.create('erp.util.GridUtil'),
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'fa/ars/saveAPBill.action',
				deleteUrl: 'fa/ars/deleteAPBill.action',
				updateUrl: 'fa/ars/updateAPBill.action',
				auditUrl: 'fa/ars/auditAPBill.action',
				resAuditUrl: 'fa/ars/resAuditAPBill.action',
				submitUrl: 'fa/ars/submitAPBill.action',
				resSubmitUrl: 'fa/ars/resSubmitAPBill.action',
				postUrl: 'fa/ars/postAPBill.action',
				resPostUrl: 'fa/ars/resPostAPBill.action',
				printUrl:'fa/ars/printAPBill.action',
				printVoucherCodeUrl:'fa/ars/printVoucherCodeAPBill.action',
				getIdUrl: 'common/getId.action?seq=APBill_SEQ',
				keyField: 'ab_id',
				codeField: 'ab_code',
				auditStatusCode:'ab_auditstatuscode',
				statusCode:'ab_statuscode',
				printStatusCode:'ab_printstatuscode',
				payStatusCode:'ab_paystatuscode',
				voucherConfig: {
					voucherField: 'ab_vouchercode',
					vs_code: 'APBill',
					yearmonth: 'ab_date',
					datas: 'ab_code',
					status: 'ab_statuscode',
					mode: 'single',
					kind: function(form){
						var f = form.down('#ab_class');
						return f ? f.getValue() : null;
					},
					vomode: 'AP'
				}
			}]
		}); 
		me.callParent(arguments); 
		me.createGrid();
		me.getAssKind();
	},
	createGrid: function() {
		var me = this;
		me.getGridSet(function(config){
			var data = config.data ? Ext.decode(config.data.replace(/,}/g, '}').replace(/,]/g, ']')) : [];
			if(data.length == 0) {
				for(var i = 0;i < 40;i++ ){
					var o = new Object();
					o.abd_detno = i + 1;
					data.push(o);
				}
			} else {
				config.fields.push({name: 'ass'});
			}
			var gridConfig = {}, cols = me.renderColumns(gridConfig, config.columns, config.necessaryFieldColor);
			var grid = Ext.create('erp.view.core.grid.Panel2', Ext.apply(gridConfig, {
				anchor: '100% 50%',
				columns: cols,
				store: new Ext.data.Store({
					fields: config.fields,
					data: data
				}),
				boxready: true,
				plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
			        clicksToEdit: 1
			    }), Ext.create('erp.view.core.plugin.CopyPasteMenu'), {
					ptype: 'rowexpander',
					expandOnDblClick: false,
					startExpand: true,
					rowBodyTpl : new Ext.XTemplate(
								'<tpl if="ass">',
									'<tpl for="ass">',
										'<div style="margin-left: 39px;">',
											'<table class="u-table">',
												'<tr><td width="200" class="text-right">{dass_assname}</td>',
												'<td width="140">{dass_codefield}<a id="{[this.onQueryClick()]}" class="u-icon x-button-icon-query" title="{dass_asstype}" rel="{dass_condid}"></a></td><td width="300">{dass_namefield}</td></tr>',
											'</table>',
										'</div>',
									'</tpl>',
								'</tpl>', {
						onQueryClick: function() {
							var id = Ext.id();
		            		Ext.defer(this.onQuery, 1, this, [id]);
		            		return id;
						},
						onQuery: function(id) {
							var elm = Ext.get(id);
		            		if(elm) {
		            			var grid = this.owner;
		            			Ext.EventManager.on(elm, {
		            				click: function(event, el) {
		            					if(!grid.readOnly) {
		            						me.queryAss(el.getAttribute('title'), el.getAttribute('rel'));
		            					}
		            					Ext.EventManager.stopEvent(event);
		            				},
		            				buffer: 100
		            			});
		            		}
						}
					}),
					renderer: function(value, metadata, record) {
		            	if(metadata)
		            		metadata.tdCls = Ext.baseCSSPrefix + 'grid-cell-special';
		            	var ass = record.get('ass') || [];
		                return ass.length > 0 ? '<div class="' + Ext.baseCSSPrefix + 'grid-row-expander">&#160;</div>' : '';
		            }
				}],
				listeners: {
					storeloaded: function(grid, data) {
						me.getAssData(grid);
					}
				},
				onExport: function() {
					this.BaseUtil.exportGrid(this);
				}
			}));
			//调用配置的renderer方法
			Ext.Array.each(grid.columns, function(column){
				if(typeof column.renderer!="function"){
			        me.GridUtil.setRenderer(grid, column);
			    }
			});
			me.insert(1, grid);
			grid.fireEvent('storeloaded', grid);
			grid.generateSummaryData();
			if(config.dbfinds && config.dbfinds.length > 0){
    			grid.dbfinds = config.dbfinds;
    		}
    		
    		Ext.defer(function(){
				var auditstatus = Ext.getCmp('ab_auditstatuscode');
				var status = Ext.getCmp('ab_statuscode');
				if(auditstatus && auditstatus.getValue() != 'ENTERING' || (status && status.getValue() != 'UNPOST')) {
					grid.readOnly = true;
					Ext.util.CSS.createStyleSheet('.u-icon{display: none !important}', 'icon_readonly');
				}
			}, 300);
		});
	},
	/**
	 * Grid配置及数据
	 */
	getGridSet: function(callback) {
		var condition = getUrlParam('gridCondition');
		condition = ((!condition || "null" == condition) ? "" : condition).replace(/IS/g, "=");
		Ext.Ajax.request({
        	url : basePath + 'common/singleGridPanel.action',
        	params: {
        		caller: caller,
        		condition: condition
        	},
        	method : 'post',
        	callback : function(opt, s, res){
        		if (res) {
        			var r = new Ext.decode(res.responseText);
            		if(r.exceptionInfo){
            			showError(res.exceptionInfo);
            		} else {
            			callback.call(null, r);
            		}
        		}
        	}
		});
	},
	renderColumns: function(gridConfig, cols, headerColor) {
		Ext.Array.each(cols, function(col){
			var logic = col.logic;
			delete col.locked;
			if(!Ext.isEmpty(logic)) {
				if(logic == 'detno'){
					gridConfig.detno = col.dataIndex;
					col.width = 40;
					col.align = 'center';
					col.renderer = function(val, meta) {
				        meta.tdCls = Ext.baseCSSPrefix + 'grid-cell-special';
				        return val;
				    };
				} else if(logic == 'keyField'){
					gridConfig.keyField = col.dataIndex;
				} else if(logic == 'mainField'){
					gridConfig.mainField = col.dataIndex;
				} else if(logic == 'orNecessField'){
					if(!gridConfig.orNecessField){
						gridConfig.orNecessField = new Array();
					}
					gridConfig.orNecessField.push(col.dataIndex);
				} else if(logic == 'necessaryField'){
					gridConfig.necessaryField = col.dataIndex;
					if(!gridConfig.necessaryFields){
						gridConfig.necessaryFields = new Array();
					}
					gridConfig.necessaryFields.push(col.dataIndex);
					if(!col.haveRendered){
						col.renderer = function(val, meta, record, x, y, store, view){
							var c = this.columns[y];
							if(val != null && val.toString().trim() != ''){
								if(c.xtype == 'datecolumn' && typeof val === 'object'){
									val = Ext.Date.format(val, 'Y-m-d');
								} else if(c.xtype == 'numbercolumn') {
									val = Ext.util.Format.number(val, c.format || '0,000.00');
								}
								return val;
							} else {
								if(c.xtype == 'datecolumn'){
									val = '';
								}
								return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
					  			'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';
							}
					   };
					}
					col.style = 'color:#' + headerColor;
				} else if(logic == 'groupField'){
					gridConfig.groupField = col.dataIndex;
				}
			}
		});
		return cols;
	},
	/**
	  * 所有核算类型 
	  */
	getAssKind: function() {
		var me = this;
		Ext.Ajax.request({
			url: basePath + 'fa/ars/assKind.action',
			method: 'GET',
			callback: function(opt, s, r) {
				if(s) {
					var data = Ext.decode(r.responseText), ks = {};
					Ext.Array.each(data, function(d){
						ks[d.AK_CODE] = d;
					});
					me.asskind = ks;
				}
			}
		});
	},
	/**
	  * 辅助核算 
	  */
	getAssData: function(grid) {
		var data = {}, has = false, abId = 0;
		grid.store.each(function(item, idx){
			abId = item.get('abd_abid');
			if(!Ext.isEmpty(item.get('ca_asstype'))){
				has = true;
				data[item.get('abd_id')] = item;
			}
		});
		if(has) {
			Ext.Ajax.request({
				url: basePath + 'fa/ars/getAPARDetailAss.action',
				params: {
					ab_id: abId,
					type: 'AP'
				},
				callback: function(opt, s, r) {
					if(s) {
						var rs = Ext.decode(r.responseText);
						if(rs.success){
		    				var ds = rs.content, x, d;
		    				for(var i in ds) {
		    					x = ds[i];
		    					d = data[x.dass_condid];
		    					var ass = d.get('ass') || [];
		    					ass.push({
		    						dass_id: x.dass_id,
		    						dass_condid: x.dass_condid,
		    						dass_asstype: x.dass_asstype,
		    						dass_assname: x.dass_assname,
		    						dass_codefield: x.dass_codefield,
		    						dass_namefield: x.dass_namefield
		    					});
		    					d.set('ass', ass);
		    					d.modified = {};
		    					d.dirty = false;
		    				}
			   			}
						grid.store.each(function(item, idx){
							if(!Ext.isEmpty(item.get('ca_asstype'))){
								var ass = item.get('ass') || [], types = item.get('ca_asstype').split('#'),names = item.get('ca_assname').split('#'), len = types.length, 
										id = item.get('abd_id');
								if(ass.length < len) {
									for(var i in types) {
										var d = Ext.Array.findBy(ass, function(t){
											return t.dass_asstype == types[i];
										});
										if(!d) {
											ass.push({dass_condid: id, dass_asstype: types[i], dass_assname: names[i]});
											if(ass.length == len)
												break;
										}
									}
									item.set('ass', ass);
								}
							}
						});
					}
				}
			});
		}
	},
	/**
	  * 查找辅助核算
	  */
	queryAss: function(type, abdId) {
		var me = this, kind = me.asskind[type];
		if(kind) {
			var search = kind.AK_DBFIND + '|' + kind.AK_ASSCODE, con = kind.AK_DBFIND == 'AssKindDetail' ? ('akd_akid=' + kind.AK_ID) : '';
			var win = new Ext.window.Window({
				id: 'dbwin',
	            title: '查找',
	            height: '80%',
	            width: '95%',
	            maximizable: true,
	            buttonAlign: 'center',
	            layout: 'anchor',
	            items: [{
	                tag: 'iframe',
	                frame: true,
	                anchor: '100% 100%',
	                layout: 'fit',
	                html: '<iframe id="iframe_dbfind" src="' + basePath + 'jsps/common/dbfind.jsp?dbfind=' + encodeURIComponent(search) + '&trigger=dbfind-ass-hidden&dbCondition=' + encodeURIComponent(con) + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
	            },{
	            	xtype: 'hidden',
	            	hidden: true,
	            	id: 'dbfind-ass-hidden',
	            	listeners: {
	            		aftertrigger: function(trigger, findRecord) {
	            			var grid = Ext.getCmp('grid'), index = grid.store.findBy(function(t){
	            				return t.get('abd_id') == abdId;
	            			}), record = grid.store.getAt(index), ass = record.get('ass') || [];
	            			for(var i in ass) {
	            				if(ass[i].dass_asstype == type) {
	            					ass[i].dass_codefield = findRecord.get(kind.AK_ASSCODE);
	            					ass[i].dass_namefield = findRecord.get(kind.AK_ASSNAME);
	            					break;
	            				}
	            			}
	            		
	            			record.set('ass', ass);
	            		}
	            	}
	            }],
	            buttons: [{
	                text: '关  闭',
	                iconCls: 'x-button-icon-close',
	                cls: 'x-btn-gray',
	                handler: function(btn) {
	                    btn.ownerCt.ownerCt.close();
	                }
	            },
	            {
	                text: '重置条件',
	                id: 'reset',
	                cls: 'x-btn-gray',
	                handler: function(btn) {
	                    var dbGrid = btn.ownerCt.ownerCt.el.dom.getElementsByTagName('iframe')[0].contentWindow.document.defaultView.Ext.getCmp('dbfindGridPanel');
	                    dbGrid.resetCondition();
	                    dbGrid.getCount();
	                }
	            }]
	        });
			win.show();
		}
	}  
});