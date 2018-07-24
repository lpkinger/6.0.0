Ext.define('erp.view.plm.project.ProjectProgress',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{xtype: 'toolbar',
				dock: 'bottom',
				ui: 'footer',
				height:35,
				items: [{
					xtype:'tbtext',
					text:'<div class="tip-block tip-block-finish"></div><div style="padding-left:2px;float:left;margin-top:-2px;">已完成</div>'
				},{
					xtype:'tbtext',
					text:'<div class="tip-block tip-block-doing"></div><div style="padding-left:2px;float:left;margin-top:-2px;">进行中</div>'
				},{
					xtype:'tbtext',
					text:'<div class="tip-block tip-block-undone"></div><div style="padding-left:2px;float:left;margin-top:-2px;">未开始</div>'  
				},'->',{
					xtype:'combo',
					emptyText:'项目名称/项目负责人',
					id:'search',
					triggerCls:'custom-rest',
					width:400,
					height:25,
					store:Ext.create('Ext.data.Store', {
						fields: [{name: 'prj_name'},
						         {name: 'prj_assignto'}],
						data:[]
					}),
					queryMode: 'local',
					enableKeyEvents:true,
					listConfig: {
		                getInnerTpl: function() {
		                    return '<div style="padding: 5px 10px;">' + 
		                    			'<span style="font-size:120%;">' +
		                    				'<tpl if="prj_name">{prj_name}<tpl else>{prj_assignto}</tpl>' +
		                    			'</span>' + 
		                    			'<span style="float:right;">{prj_assignto}</span>' + 
		                    		'</div>';
		                }
		            },
		            doLocalQuery: function(queryPlan) {
		                var me = this,
		                    queryString = queryPlan.query;

		                // Create our filter when first needed
		                if (!me.queryFilter) {
		                    // Create the filter that we will use during typing to filter the Store
		                    me.queryFilter = new Ext.util.Filter({
		                        id: me.id + '-query-filter',
		                        anyMatch: true,
		                        caseSensitive: me.caseSensitive,
		                        root: 'data',
		                        property: "prj_name|prj_assignto",
		                        createFilterFn: function() {
				    		        var me       = this,
				    		            matcher= me.createValueMatcher(),
				    		            property = me.property;
				    		            property1= me.property.split('|')[0];
				    		            property2= me.property.split('|')[1];
				    		            me.matcher=matcher;
				    		            
				    		        if (me.operator) {
				    		            return me.operatorFns[me.operator];
				    		        } else {
				    		            return function(item) {
				    		                var record = me.getRoot(item),value1=record[property1],value2=record[property2];	
				    		                return matcher === null ? value === null : matcher.test(value1) || matcher.test(value2) ;
				    		            };
				    		        }
				    		    }
		                    });
		                    me.store.addFilter(me.queryFilter, false);
		                }
		                if (queryString || !queryPlan.forceAll) {
		                    me.queryFilter.disabled = false;
		                    me.queryFilter.setValue(me.enableRegEx ? new RegExp(queryString) : queryString);
		                }
		                else {
		                    me.queryFilter.disabled = true;
		                }
		                me.store.filter();
		                if (me.store.getCount()) {
		                    me.expand();
		                } else {
		                    me.collapse();
		                }

		                me.afterQuery(queryPlan);
		            },
					listeners :{
						render:function(c){										   
							c.bodyEl.applyStyles('border:solid 1px rgb(181, 184, 200);');
							c.inputEl.applyStyles('border-width:0;background:0px 0px repeat-x white;');
						},
						change:function(c,newvalue){
							if(newvalue) c.getEl().down("." + c.triggerCls).applyStyles({visibility: 'visible'});
							else {
								c.getEl().down("." + c.triggerCls).applyStyles({visibility: 'hidden'});
								Ext.getCmp('progressgrid').getStore().clearFilter();

							}
						},
						select:function(combo,records){
							var grid=Ext.getCmp('progressgrid'),record=records[0],property='prj_name',value;
							if(combo && combo.queryFilter.matcher){
								if(combo.queryFilter.matcher.test(record.get('prj_name'))){
									property='prj_name',value=record.get('prj_name');
									combo.setValue(value);
								}else {
									
									property='prj_assignto',value=record.get('prj_assignto');
								}
								
							}
							if(!value) value=record.get('prj_name');
							combo.setValue(value);
							grid.getStore().filter([{property: property, value:value}]);							 
						},
						keypress:function(f,e){
						    if(e.keyCode == e.ENTER){
						    	var grid=Ext.getCmp('progressgrid');
						    	grid.getStore().filter([{property: "prj_name|prj_assignto", value:f.rawValue,
						    		 createFilterFn: function() {
						    		        var me       = this,
						    		            matcher  = me.createValueMatcher(),
						    		            property = me.property;
						    		            property1= me.property.split('|')[0];
						    		            property2= me.property.split('|')[1];
						    		        if (me.operator) {
						    		            return me.operatorFns[me.operator];
						    		        } else {
						    		            return function(item) {
						    		                var record = me.getRoot(item),value1=record[property1],value2=record[property2];	
						    		                return matcher === null ? value === null : matcher.test(value1) || matcher.test(value2) ;
						    		            };
						    		        }
						    		    }
						    	}]);
						    }
						}
					},
					onTriggerClick:function(e){
						this.setValue(null);

					}
				},'->',{
					name: 'query',
					id: 'query',
					text: $I18N.common.button.erpQueryButton,
					iconCls: 'x-button-icon-query',
					margin: '0 4 0 0',
					height:25
				},'-',{
					text: $I18N.common.button.erpCloseButton,
					iconCls: 'x-button-icon-close',						
					margin:'0 20 0 0',
					height:25,
					handler: function(){
						var main = parent.Ext.getCmp("content-panel"); 
						main.getActiveTab().close();
					}
				}]},
				{
					xtype: 'progressgrid',  
					anchor: '100% 95%'
				}] 
		}); 
		me.callParent(arguments); 
	} 
});