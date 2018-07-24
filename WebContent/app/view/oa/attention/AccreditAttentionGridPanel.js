Ext.define('erp.view.oa.attention.AccreditAttentionGridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpAccreditAttentionGridPanel',
	id: 'AttentionGridPanel', 
	layout : 'fit',
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: [],
    multiselected: [],
    bodyStyle: 'background: #f1f1f1;',
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }),
	initComponent : function(){ 
		condition = this.BaseUtil.getUrlParam('urlcondition');
		condition = (condition == null) ? "1=1": condition;
		condition = condition.replace(/@/,"'%").replace(/@/,"%'");
		this.defaultCondition = condition;
		var gridParam = {caller: "AccreditPersonAttention", condition: condition,page: page,pageSize: pageSize};
    	this.getGridColumnsAndStore(this, 'oa/attention/getAttentionDataAndColumns.action?', gridParam, "");
    	this.callParent(arguments); 
	},
	getGridColumnsAndStore: function(grid, url, param, no){
		var me = this;
		var main = parent.Ext.getCmp("content-panel");
		if(!main)
			main = parent.parent.Ext.getCmp("content-panel");
		if(main){
			main.getActiveTab().setLoading(true);//loading...
		}
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + url,
        	params: param,
        	method : 'post',
        	async: true,
        	callback : function(options,success,response){
        		if(main){
        			main.getActiveTab().setLoading(false);
        		}
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.columns){
        			Ext.each(res.columns, function(column, y){
        				//render
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
                    		}
                    		column.renderer = grid.RenderUtil[renderName];
                    		column.haveRendered = true;
                    	}
                    });
                    }
            		var data = [];
            		if(!res.data || res.data.length == 2){
            			me.add10EmptyData(grid.detno, data);
            			me.add10EmptyData(grid.detno, data);//添20条吧
            		} else {
            			data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
            			//me.add10EmptyData(grid.detno, data);
            		}
            		var store = Ext.create('Ext.data.Store', {
            		    fields: res.fields,
            		    data: data,    		   
            		});
            		
            		if(grid.selModel.views == null){
            			grid.selModel.views = [];
            		}
            		
            		grid.reconfigure(store, res.columns); 
        		    grid.mergeCells(grid,[1,2,3,4]);
                     /**setTimeout(function(){
                     grid.mergeCells(grid,[1,2,3,4]);
                    },10000);**/
            		}
        });
	},
	viewConfig: {
        stripeRows: true
    },

	/**
	 * 从index行开始，往grid里面加十空行
	 * @param detno 编号字段
	 * @param data 需要添加空白数据的data
	 */
	add10EmptyData: function(detno, data){
		if(detno){
			var index = data.length == 0 ? 0 : Number(data[data.length-1][detno]);
			for(var i=0;i<10;i++){
				var o = new Object();
				o[detno] = index + i + 1;
				data.push(o);
			}
		} else {
			for(var i=0;i<10;i++){
				var o = new Object();
				data.push(o);
			}
		}
	},
    mergeCells:function(grid,cols){  
    var arrayTr=document.getElementById(grid.getId()+"-body").firstChild.firstChild.firstChild.getElementsByTagName('tr');    
    var trCount = arrayTr.length;  
    var arrayTd;  
    var td;  
    var merge = function(rowspanObj,removeObjs){ //定义合并函数  
        if(rowspanObj.rowspan != 1){  
            arrayTd =arrayTr[rowspanObj.tr].getElementsByTagName("td"); //合并行  
            td=arrayTd[rowspanObj.td-1];  
            td.rowSpan=rowspanObj.rowspan;  
            td.vAlign="middle";
            td.style="text-align:center";               
            Ext.each(removeObjs,function(obj){ //隐身被合并的单元格  
                arrayTd =arrayTr[obj.tr].getElementsByTagName("td");  
                arrayTd[obj.td-1].style.display='none';                           
            });  
        }     
    };    
    var rowspanObj = {}; //要进行跨列操作的td对象{tr:1,td:2,rowspan:5}      
    var removeObjs = []; //要进行删除的td对象[{tr:2,td:2},{tr:3,td:2}]  
    var col;  
    Ext.each(cols,function(colIndex){ //逐列去操作tr  
        var rowspan = 1;  
        var divHtml = null;         
        for(var i=1;i<trCount;i++){
            arrayTd = arrayTr[i].getElementsByTagName("td");  
            var cold=0;  
//          Ext.each(arrayTd,function(Td){ //获取RowNumber列和check列  
//              if(Td.getAttribute("class").indexOf("x-grid-cell-special") != -1)  
//                  cold++;                               
//          });  
            col=colIndex+cold;//跳过RowNumber列和check列  
            if(!divHtml){  
                divHtml = arrayTd[col-1].innerHTML;  
                rowspanObj = {tr:i,td:col,rowspan:rowspan}  
            }else{  
                var cellText = arrayTd[col-1].innerHTML;  
                var addf=function(){   
                    rowspanObj["rowspan"] = rowspanObj["rowspan"]+1;  
                    removeObjs.push({tr:i,td:col});  
                    if(i==trCount-1)  
                        merge(rowspanObj,removeObjs);
                };  
                var mergef=function(){  
                    merge(rowspanObj,removeObjs);
                    divHtml = cellText;  
                    rowspanObj = {tr:i,td:col,rowspan:rowspan}  
                    removeObjs = [];  
                };  
                if(cellText == divHtml){  
                    if(colIndex!=cols[0]){   
                        var leftDisplay=arrayTd[col-2].style.display;
                        if(leftDisplay=='none')  
                            addf();   
                        else  
                            mergef();                             
                    }else  
                        addf();                                           
                }else  
                    mergef();             
            }  
        }  
    });   
}  
});