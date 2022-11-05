package migration.data

trait NamingStrategy

class Schema[TableTypes, DataType, RestrictionType](
    name: String,
    tables: Map[Any, Table[? <: TableTypes, DataType, RestrictionType]],
    naming: NamingStrategy,
  )

final case class Table[TableType, DataType, RestrictionType](
    name: String,
    columns: Seq[Column[DataType, RestrictionType]],
  )

final case class Column[+DataType, +RestrictionType](
    name: String,
    dataType: DataType,
    restrictions: Seq[RestrictionType],
  )


// Schema[Person & Address, _, _]