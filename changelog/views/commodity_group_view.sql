SELECT cg.description            AS commodity_group,
       cgc.traces_commodity_code AS traces_commodity_code
from commodity_group cg
         inner join commodity_group_commodity cgc
                    on cg.code = cgc.commodity_group_code;