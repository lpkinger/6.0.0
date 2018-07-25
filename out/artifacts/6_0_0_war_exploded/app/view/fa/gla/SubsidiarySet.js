Ext.define('erp.view.fa.gla.SubsidiarySet',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				xtype: 'form',
				title : '子公司设置',
				frame : true,
				region: 'north',
				html: '<div id="container"><div id="content">'+
				'<div style="line-height:27px;font-size:13px;margin:auto;text-align:left;">' +
				'<div>1、设置哪些子账套参与合并报表（可添加其它公司） </div>'+
				'<div>2、设置子账套的控股公司和控股比例</div></div></div></div>',
				bbar:['->',{
					xtype: 'erpUpdateButton'
				},{
					xtype:'erpCloseButton'
				},'->']
			},{
				xtype:'gridpanel',
				region: 'center',
				layout : 'fit',
				id:'mainset',
				plugins : [Ext.create('Ext.grid.plugin.CellEditing', {
			        clicksToEdit : 1
			    })],
				autoScroll : true,
			    columnLines : true,
			    store: Ext.create('Ext.data.Store',{
					fields:['ss_id','ss_enable','ss_detno','ss_mastercode','ss_mastername', 'ss_name', 'ss_man', 'ss_date', 'ss_currency']
				}),
				columns:[
					{
						header:'启用状态',
						dataIndex:'ss_enable',
						width:65,
						cls : 'x-grid-header-1',
						xtype:'actioncolumn',
						processEvent: function(type, view, cell, recordIndex, cellIndex, e) {
					        if (type == 'mousedown' || (type == 'keydown' && (e.getKey() == e.ENTER || e.getKey() == e.SPACE))) {
					        	var record = null;
					        	var dataIndex = this.dataIndex;
					        	var checked = null;					        	
				        		record = view.panel.store.getAt(recordIndex);
				        		checked = !record.get(dataIndex);
					            record.set(dataIndex, checked);
					            this.fireEvent('checkchange', this, recordIndex, checked);
					            return false;
					        }
					    },
						renderer:function(value, m, record){
					        var cssPrefix = Ext.baseCSSPrefix,
					            cls = [cssPrefix + 'grid-enableheader'];
					        if (value) {
					            cls.push(cssPrefix + 'grid-enableheader-checked');
					        }
					        return '<div class="' + cls.join(' ') + '">&#160;</div>';
					    }
					},{
						header:'序号',
						dataIndex:'ss_detno',
						align:'center',
						cls : 'x-grid-header-1',
						style:'color:#FF0000',
						necessField:true,
						width:65,
						editor:{
							xtype:'textfield'
						}
					},{
						header:'账套编号',
						dataIndex:'ss_mastercode',
						cls : 'x-grid-header-1',
						width:150,
						editor:{
							xtype:'textfield'
						}
					},{
						header:'账套名称',
						dataIndex:'ss_mastername',
						style:'color:#FF0000',
						cls : 'x-grid-header-1',
						necessField:true,
						width:150,
						editor:{
							xtype:'textfield'
						}
					},{
						header:'公司名称',
						dataIndex:'ss_name',
						cls : 'x-grid-header-1',
						width:350,
						editor:{
							xtype:'textfield'
						}
					},{
						header:'本币币别',
						dataIndex:'ss_currency',
						style:'color:#FF0000',
						cls : 'x-grid-header-1',
						necessField:true,
						width:100,
						editor:{
							xtype:'textfield'
						},
						dbfind:'Currencys|cr_name',
						editor:{
							  xtype:'dbfindtrigger',
							  hideTrigger: false,
							  name:'ss_currency',
							  which:'grid',
							  dbfind:'Currencys|cr_name',
							  listeners: {
				                  aftertrigger: function(t, d) {
				                	  var record = Ext.getCmp('mainset').selModel.lastSelected;  				
								       record.set('ss_currency', d.data.cr_name);
				                  }
							  }
						},
					},{
						xtype:'actioncolumn',
						align:'center',
						header:'股东设置',
						id:'set',
						width:100,
						cls : 'x-grid-header-1',
						items:[{							
							tooltip:'股东设置',
							align:'center',
							id: 'paramset_',
							icon : basePath + 'resource/images/set/errorset.png',
							getClass:function(v,meta,r,rowIndex,colIndex,store){
								return 'paramset';
							},
							handler: function(grid, rowIndex, colIndex, item) {  
		                       var rec = grid.getStore().getAt(rowIndex);  
		                       this.fireEvent('paramset',  grid, rec);  
		                   }  
						}]
					},{
						header:'最近更新人',
						dataIndex:'ss_man',
						cls : 'x-grid-header-1',
						width:80
					},{
						header:'最近更新日期',
						dataIndex:'ss_date',
						cls : 'x-grid-header-1',
						width:110
					},{
						header:'ID',
						dataIndex:'ss_id',
						cls : 'x-grid-header-1',
						width:0
					}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});