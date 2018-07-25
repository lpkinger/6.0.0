Ext.define('Ext.ux.PreviewPlugin', {
	extend: 'Ext.AbstractPlugin',
	alias: 'plugin.preview',
	requires: ['Ext.grid.feature.RowBody', 'Ext.grid.feature.RowWrap'],

	// private, css class to use to hide the body
	hideBodyCls: 'x-grid-row-body-hidden',

	/**
	 * @cfg {String} bodyField
	 * Field to display in the preview. Must be a field within the Model definition
	 * that the store is using.
	 */
	bodyField: '',

	/**
	 * @cfg {Boolean} previewExpanded
	 */
	previewExpanded: true,

	setCmp: function(grid) {
		this.callParent(arguments);
		/*'<div class="flow-item flow-item-{[xindex-1]}">',
           '<div>{PP_PHASE}</div>',
      '</div>',*/
		var bodyField   = this.bodyField,
		hideBodyCls = this.hideBodyCls,
		features    = [{
			ftype: 'rowbody',    
			progressTpl:new Ext.XTemplate(
					'<div id="progress" class="progress">',
					'<tpl for=".">',
					'<tpl if="this.isFinish(PP_STATUS)">',
					'<div class="flow-item flow-item-{[xindex===1?"0-finish":"finish"]}">',
					'<tpl elseif="this.isDone(PP_STATUS)">',
					'<div class="flow-item flow-item-{[xindex===1?"0-doing":"doing"]}">',
					'<tpl else>',
					'<div class="flow-item flow-item-undone">',
					'</tpl>',
					'<div>{PP_PHASE}</div></div>',
					'</tpl></div>',
					{

						disableFormats: true,
						isFinish: function(PP_STATUS){
							return PP_STATUS == '已完成';
						},
						isDone: function(PP_STATUS){
							return PP_STATUS=='进行中';
						}
					}
			),
			getAdditionalData: function(data, idx, record, orig, view) {
				var project_phases=record.get('project_phases');
				var tpl=this.progressTpl.applyTemplate(
						project_phases
				);                	
				var getAdditionalData = Ext.grid.feature.RowBody.prototype.getAdditionalData,
				additionalData = {
						rowBody: tpl,
						rowBodyCls: grid.previewExpanded ? '' : hideBodyCls
				};

				if (getAdditionalData) {
					Ext.apply(additionalData, getAdditionalData.apply(this, arguments));
				}
				return additionalData;
			},
			extraRowTpl: [
			              '{%',
			              'values.view.rowBodyFeature.setupRowData(values.record, values.recordIndex, values);',
			              'this.nextTpl.applyOut(values, out, parent);',
			              '%}',
			              '<tr class="' + Ext.baseCSSPrefix + 'grid-rowbody-tr {rowBodyCls}">',
			              '<td class="' + Ext.baseCSSPrefix + 'grid-cell-rowbody' + '" colspan="{rowBodyColspan}">',
			              '<div class="' + Ext.baseCSSPrefix + 'grid-rowbody' + ' {rowBodyDivCls}">{rowBody}</div>',
			              '</td>',
			              '</tr>', {
			            	  priority: 100,
			            	  syncRowHeights: function(firstRow, secondRow) {
			            		  var owner = this.owner,
			            		  firstRowBody = Ext.fly(firstRow).down(owner.eventSelector, true),
			            		  secondRowBody,
			            		  firstHeight, secondHeight;

			            		  if (firstRowBody && (secondRowBody = Ext.fly(secondRow).down(owner.eventSelector, true))) {
			            			  if ((firstHeight = firstRowBody.offsetHeight) > (secondHeight = secondRowBody.offsetHeight)) {
			            				  Ext.fly(secondRowBody).setHeight(firstHeight);
			            			  }
			            			  else if (secondHeight > firstHeight) {
			            				  Ext.fly(firstRowBody).setHeight(secondHeight);
			            			  }
			            		  }
			            	  },

			            	  syncContent: function(destRow, sourceRow) {
			            		  var owner = this.owner,
			            		  destRowBody = Ext.fly(destRow).down(owner.eventSelector, true),
			            		  sourceRowBody;


			            		  if (destRowBody && (sourceRowBody = Ext.fly(sourceRow).down(owner.eventSelector, true))) {
			            			  Ext.fly(destRowBody).syncContent(sourceRowBody);
			            		  }
			            	  }
			              }
			              ],

		}, {
			ftype: 'rowwrap'
		}];

		grid.previewExpanded = this.previewExpanded;
		if (!grid.features) {
			grid.features = [];
		}
		grid.features = features.concat(grid.features);
	},

	/**
	 * Toggle between the preview being expanded/hidden
	 * @param {Boolean} expanded Pass true to expand the record and false to not show the preview.
	 */
	toggleExpanded: function(expanded) {
		var view = this.getCmp();
		this.previewExpanded = view.previewExpanded = expanded;
		view.refresh();
	}
});
Ext.define('erp.view.plm.project.ProgressGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.progressgrid',
	layout: 'fit', 
	id:'progressgrid',
	hideBorders: true,
	columns: [],
	columnLines:false,
	autoScroll:true,
	viewConfig: {
		stripeRows: true,
		enableTextSelection: true,//允许选中文字
		plugins: [{
			ptype: 'preview',
			bodyField: 'prj_code',
			expanded: true,
			pluginId: 'preview'
		}],
		listeners:{
			rowbodyclick:function(v,i,e){
				var target=e.target,record=v.getSelectionModel().lastSelected,phases=record.get('project_phases');
				if(target){
                    var d=Ext.get(target.parentElement);
					if(d.hasCls('flow-item')){
					  var text=d.dom.textContent,data;
					  Ext.Array.each(phases,function(o){
						 if(o.PP_PHASE ==text){
							 data=o;
							 return false;
						 } 
					  });
					  var tip = this.tip;
					   if (!tip) {
						   tip = this.tip = Ext.widget('quicktip', {
							   target:target,
							   title: '阶段详情:',
							   autoHide: false,
							   anchor: 'top',
							   mouseOffset: [10, -5],
							   closable: true,
							   minHeight:150,
							   constrainPosition: true,
							   cls: 'errors-tip',
							   style:'background-color:#E5E5E5!important',
							   tpl:new Ext.XTemplate(
									    '<p style="padding-top:10px;">阶段: {PP_PHASE}</p>',
									    '<p>计划开始日期: {PP_STARTDATE}</p>',
									    '<p>计划结束日期: {PP_ENDDATE}</p>',									    
									    '<tpl for=".">',
									    	'<tpl if="PP_STATUS==\'已完成\'">', 
									         '<p>实际开始日期: {PP_REALSTARTDATE}</p>',
									         '<p>实际结束日期: {PP_REALENDDATE}</p>',
							               '</tpl>',
							               	'<tpl if="PP_STATUS==\'进行中\'">', 
									         '<p>实际开始日期: {PP_REALSTARTDATE}</p>',
							               '</tpl>',
							               '<p>负责人: {PP_CHARGEPERSON}</p>',
									       '<tpl if="PP_STATUS!=undefined">', 
									         '<p style="font-weight:600;">状态: {PP_STATUS}</p>',
							               '</tpl>',
								       '</tpl>'
									   
							   )
						   });	    				
						  
					   }
					  tip.update(data);
					  tip.setTarget(target);
					  tip.show();
					}
					 
				}
			}
		}
	},
	initComponent : function(){ 
		var me=this;	
		me.getGridColumnsAndStore(this);
		me.callParent(arguments); 
	},
	getGridColumnsAndStore: function(grid, url, param, no){
		var me = this;
		var param={
				caller:this.caller,
				condition:'1=1'
		};
		//this.getGridColumnsAndStore(this, 'common/singleGridPanel.action', params, "");
		grid.setLoading(true);
		Ext.Ajax.request({
			url:basePath + 'common/datalist.action',
			params: {
				caller: 'ProjectGant',
				condition:'1=2',
				page: 1,
				pageSize: 2000
			},
			async: (grid.sync ? false : true),
			method : 'post',
			callback : function(options,success,response){
				grid.setLoading(false);
				if (!response) return;
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}

				if(res.columns){
					var limits = res.limits, limitArr = new Array();
					if(limits != null && limits.length > 0) {//权限外字段
						limitArr = Ext.Array.pluck(limits, 'lf_field');
					}
					Ext.each(res.columns, function(column, y){
						if(column.dataIndex.indexOf(' ') > -1) {
							column.dataIndex = column.dataIndex.split(' ')[1];
						}
						if(column.xtype=='combocolumn') delete column['xtype'];
						if(column.dataIndex && column.dataIndex.toUpperCase()=='PRJ_NAME'){
							column.renderer=function(value, p, record) {
		    					if(value) {
		    						 return Ext.String.format(
		    								'<a href="javascript:void(0)" onclick="openTable(\'{1}\',\'jsps/plm/project/ProjectMessage.jsp?formCondition=prj_idIS{0}&prjCode={2}\',\'{0}\',\'single\')">{1}</a>',
		 	    			                record.get('prj_id'),
		 	    			                value,record.get('prj_code')
		 	    			          );
			    			    };
			    			};
						}
					});
					var data = [];
					if(res.data && res.data.length > 2){
						data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
					}
					res.fields.push({
						name:'project_phases'	
					});
					var store=new Ext.data.Store({
						fields: res.fields,
						data:[],
						createFilterFn: function(property, value, anyMatch, caseSensitive, exactMatch) {
					        if (Ext.isEmpty(value)) {
					            return false;
					        }
					        value = this.data.createValueMatcher(value, anyMatch, caseSensitive, exactMatch);
					        return function(r) {
					            return value.test(r.data[property]);
					        };
					    }
					});
					if(grid.selModel && grid.selModel.views == null){
						grid.selModel.views = [];
					}
					if(res.dbfinds && res.dbfinds.length > 0){
						grid.dbfinds = res.dbfinds;
					}
					if(res.columns) Ext.Array.insert( res.columns, 0,[{
						width:60,
						renderer:function(val,meta,record){
							var reval = null;
							var enddate = null;
							var start = false;
							Ext.Array.each(record.data.project_phases,function(item,index){
								if(item.PP_STATUS=='进行中'){
									enddate = item.PP_ENDDATE;
								}
							});
							meta.tdAttr ='rowspan="3"';
							var extCurrentDate = Ext.Date.format(new Date,'Y-m-d');
							var extEnddate = Ext.Date.format(new Date(enddate),'Y-m-d');
							var over = false;
							if(enddate&&extEnddate<extCurrentDate){
								over = true;
							}
							if(record.data.prj_status=='未启动'&&!over){
								return '<div class="color-column-inner" style="background-color:#90A4AE;margin-top:15px;" data-qtip="项目未启动" align="center">&nbsp;</div>';
							}else if(enddate&&over){
								return '<div class="color-column-inner" style="background-color:red;margin-top:15px;" data-qtip="项目超时" align="center">&nbsp;</div>';
							}else return  '<div class="color-column-inner" style="background-color:green;margin-top:15px;" data-qtip="项目正常" align="center">&nbsp;</div>';
						}           

					}]);					
					grid.reconfigure(store, res.columns);

				} else {
					grid.hide();
				}
			}
		});
	},
	loadNewStore:function(grid,condition){
		grid.setLoading(true);
		condition=condition?condition:'1=1';
		Ext.Ajax.request({
			url:basePath + 'common/datalist/data.action',
			params: {
				caller: 'ProjectGant',
				condition:condition,
				page: 1,
				pageSize: 2000
			},
			async: (grid.sync ? false : true),
			method : 'post',
			callback : function(options,success,response){
				grid.setLoading(false);
				if (!response) return;
				var phases=grid.getPhases(condition);
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}
				Ext.Array.each(res.data,function(record){
					record['project_phases']=phases[record.prj_id];
				});
				grid.setSearchData(res.data);
				grid.getStore().loadData(res.data);
			}
		});

	},
	setSearchData:function(data){
		var search=Ext.getCmp('search');
		search.getStore().loadData(data);
	},
	getPhases:function(condition){
		var data=null;
		Ext.Ajax.request({
			url:basePath + 'plm/project/getPhases.action',
			params: {
				caller: 'ProjectGant',
				condition:condition,
			},
			async:false,
			method : 'post',
			callback : function(options,success,response){
				if (!response) return;
				data = new Ext.decode(response.responseText);

			}
		});
		return data;
	},
	filterData:function(value){
		var me=this,store=this.getStore();this.filterValue=value;
		store.filterBy(function(record,value){
			
		},me);
	}


});