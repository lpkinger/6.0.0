Ext.define('erp.view.common.multiDbfind.ResultGridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpResultDbfindGridPanel',
	layout : 'fit',
	hidden:true,
	id: 'dbfindresultgrid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: [],
    selectAll:true,
    selectObject:new Object(),
    bbar:[{xtype:'tbtext',id:'list2_summary'},'->',{id:'count',xtype: 'tbtext',text: '共: 0  条, 已选: 0  条'}],
    selModel: Ext.create('Ext.selection.CheckboxModel',{
	    	ignoreRightMouseSelection : false,
	    	checkOnly: true,
			listeners:{
	            selectionchange:function(selModel, selected, options){//表头全选、取消全选
	            	var grid=selModel.view.ownerCt;
	            	if(grid.selectAll){
	            		if(selected.length>0){//全选
	            			Ext.each(selected,function(select){
		            			grid.selectObject[Ext.JSON.encode(select.data)]=select.data;
	            			});
	            		}else{//取消全选
	            			var grid=selModel.view.ownerCt;
	            			Ext.each(grid.store.data.items,function(deselect){
		            			var key=Ext.JSON.encode(deselect.data);
		            			delete grid.selectObject[key];
	            			});
	            		}
		            	grid.updateInfo();
	            	}else{
	            	 	grid.selectAll=true;
	            	}
	            },
	            select:function(selModel, record, index, opts){//选中
	            	var grid=selModel.view.ownerCt;
	            	if(grid.selectAll){
		            	grid.selectObject[Ext.JSON.encode(record.data)]=record.data;
	            		grid.selectAll=false;
	            	}
	            	grid.updateInfo();
	            },
	            deselect:function(selModel, record, index, opts){//取消选中
	            	var grid=selModel.view.ownerCt;
	            	if(grid.selectAll){
		            	var key=Ext.JSON.encode(record.data);
			            delete grid.selectObject[key];
			            grid.selectAll=false;
	            	}
	            	grid.updateInfo();
	            }
	        }
	}),
	headerCt: Ext.create("Ext.grid.header.Container",{
 	    forceFit: false,
        sortable: true,
        enableColumnMove:true,
        enableColumnResize:true,
        enableColumnHide: true
     }),
	plugins: [Ext.create('Ext.ux.grid.GridHeaderFilters')],
	initComponent : function(){ 
		this.callParent(arguments);
	},
	updateInfo:function(){
		var grid=this;
		var count_all=grid.store.data.items.length;
		var count_select=grid.selModel.getSelection().length;
		var count = Ext.getCmp('count');
		if (count) count.setText('共: ' + count_all + ' 条, 已选: ' + count_select+ ' 条');
		//hey start 合计栏
		var columns = grid.columns;			    				    	
    	var items = [];
		if(columns){
			Ext.Array.each(columns,function(column){	
				if(column.summaryType){
					//合计
					if(column.summaryType=='sum'){
						if(count_select!=0){
							var sum = 0;
							Ext.Array.each(grid.selModel.getSelection(),function(row){		
								sum+=parseFloat(row.data[column.dataIndex]);
							});
							items.push(column.text + ':'+Ext.util.Format.number(sum,'0,000.00'));
						}else{
							items.push(column.text + ':'+Ext.util.Format.number(0,'0,000.00'));
						}	
					}
					//条数
					if(column.summaryType=='count'){
						items.push(column.text + ':'+count_select+'条');
					}
					//平均数
					if(column.summaryType=='avergae'){	
						var sum = 0;
						if(count_select>0){						
				    		Ext.Array.each(grid.selModel.getSelection(),function(row){
								sum+=parseFloat(row.data[column.dataIndex]);
							});
				    		items.push(column.text + ':'+Ext.util.Format.number(sum/count_select,'0,000.00'));			    		
				    	}else{
				    		items.push(column.text + ':'+Ext.util.Format.number(0,'0,000.00'));
				    	}					
					}
				}
			});
		}	
		if(items.length>0) Ext.getCmp('list2_summary').update(items.join(" | "));
		//hey end
	},
	RenderUtil: Ext.create('erp.util.RenderUtil'),
	selectDefault:function(){
				var grid=this;	
		 		var datachecked=new Array();
				Ext.each(Ext.Object.getKeys(grid.selectObject),function(k){
					datachecked.push(grid.selectObject[k]);
				});
				if(datachecked.length>0){
					var selectArr=new Array();
					Ext.each(grid.store.data.items, function(item){
						delete item.data.RN;
						Ext.each(datachecked,function(checked){
							var checkflag=true;
							var keys=Ext.Object.getKeys(item.data);
							for(var i=0;i<keys.length && checkflag;i++){
								var k=keys[i];
								if(item.data[k]!=checked[k]){
									checkflag=false;
								}
								if(i==keys.length-1&&checkflag){
									selectArr.push(item);
								}
							} 					
						});
					});
					grid.selectAll=false;
					grid.selModel.deselectAll();
					grid.selModel.select(selectArr);
				}
	
	},
	setDefaultColumns:function(fields,columns){
		Ext.Array.each(columns, function(column, y) { 
			if(column.xtype='combocolumn'){
				column.xtype='';
				column.filter.xtype='textfield';
			}
		});
		this.reconfigure(Ext.create('Ext.data.Store', {
            fields: fields,
            data: [],
            listeners:{
               'datachanged':function(){
                	Ext.getCmp('dbfindresultgrid').selectDefault();
          		 }
             }
        }), columns);
	},
	listeners: {
		'headerfiltersapply': function(grid, filters) {
		 	grid.selectAll=false;
		},
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	}
});