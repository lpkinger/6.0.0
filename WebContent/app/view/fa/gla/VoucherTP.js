Ext.define('erp.view.fa.gla.VoucherTP',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, {
			items: [{
				anchor: '100% 23%',
				xtype: 'erpFormPanel',
				saveUrl: 'fa/gla/saveVoucherTP.action',
				deleteUrl: 'fa/gla/deleteVoucherTP.action',
				updateUrl: 'fa/gla/updateVoucherTP.action',
				getIdUrl: 'common/getId.action?seq=VOUCHER_TP_SEQ',
				keyField: 'vo_id',
				codeField: 'vo_code'
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
					o.vd_detno = i + 1;
					data.push(o);
				}
			} else {
				config.fields.push({name: 'ass'});
			}
			var gridConfig = {}, cols = me.renderColumns(gridConfig, config.columns);
			var grid = Ext.create('erp.view.core.grid.Panel2', Ext.apply(gridConfig, {
				anchor: '100% 77%',
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
												'<tr><td width="200" class="text-right">{vds_asstype}</td>',
												'<td width="140">{vds_asscode}<a id="{[this.onQueryClick()]}" class="u-icon x-button-icon-query" title="{vds_asstype}" rel="{vds_vdid}"></a></td><td width="300">{vds_assname}</td></tr>',
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
				}]
			}));
			me.insert(1, grid);
			grid.fireEvent('storeloaded', grid);
			if(config.dbfinds && config.dbfinds.length > 0){
    			grid.dbfinds = config.dbfinds;
    		}
			me.getAssData(grid);
			Ext.defer(function(){
				var status = Ext.getCmp('vo_statuscode');
				if(status && status.getValue() != 'ENTERING' && status.getValue() != 'UNPOST') {
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
	renderColumns: function(gridConfig, cols) {
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
						ks[d.AK_NAME] = d;
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
		var data = {}, has = false;
		grid.store.each(function(item, idx){
			if(!Ext.isEmpty(item.get('ca_asstype'))){
				has = true;
				data[item.get('vd_id')] = item;
			}
		});
		if(has) {
			Ext.Ajax.request({
				url: basePath + 'common/getFieldsDatas.action',
				params: {
					caller: 'VoucherDetailAss_TP',
					fields: 'vds_id,vds_vdid,vds_asstype,vds_asscode,vds_assname',
					condition: 'vds_vdid in (' +Ext.Object.getKeys(data).join(',') + ') order by vds_vdid,vds_detno'
				},
				callback: function(opt, s, r) {
					if(s) {
						var rs = Ext.decode(r.responseText);
						if(rs.success){
		    				var ds = Ext.decode(rs.data), x, d;
		    				for(var i in ds) {
		    					x = ds[i];
		    					d = data[x.VDS_VDID];
		    					var ass = d.get('ass') || [];
		    					ass.push({
		    						vds_id: x.VDS_ID,
		    						vds_vdid: x.VDS_VDID,
		    						vds_asstype: x.VDS_ASSTYPE,
		    						vds_asscode: x.VDS_ASSCODE,
		    						vds_assname: x.VDS_ASSNAME
		    					});
		    					d.set('ass', ass);
		    					d.modified = {};
		    					d.dirty = false;
		    				}
			   			}
						grid.store.each(function(item, idx){
							if(!Ext.isEmpty(item.get('ca_asstype'))){
								var ass = item.get('ass') || [], names = item.get('ca_assname').split('#'), len = names.length, 
										id = item.get('vd_id');
								if(ass.length < len) {
									for(var i in names) {
										var d = Ext.Array.findBy(ass, function(t){
											return t.vds_asstype == names[i];
										});
										if(!d) {
											ass.push({vds_vdid: id, vds_asstype: names[i]});
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
	queryAss: function(type, vdId) {
		var me = this, kind = me.asskind[type];
		if(kind) {
			var search = kind.AK_DBFIND + '|' + kind.AK_ASSCODE;
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
	                html: '<iframe id="iframe_dbfind" src="' + basePath + 'jsps/common/dbfind.jsp?dbfind=' + encodeURIComponent(search) + '&trigger=dbfind-ass-hidden" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
	            },{
	            	xtype: 'hidden',
	            	hidden: true,
	            	id: 'dbfind-ass-hidden',
	            	listeners: {
	            		aftertrigger: function(trigger, findRecord) {
	            			var grid = Ext.getCmp('grid'), index = grid.store.findBy(function(t){
	            				return t.get('vd_id') == vdId;
	            			}), record = grid.store.getAt(index), ass = record.get('ass') || [];
	            			for(var i in ass) {
	            				if(ass[i].vds_asstype == type) {
	            					ass[i].vds_asscode = findRecord.get(kind.AK_ASSCODE);
	            					ass[i].vds_assname = findRecord.get(kind.AK_ASSNAME);
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