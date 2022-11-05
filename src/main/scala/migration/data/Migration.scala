package migration.data

enum Migration[+TableType, +DataType, +RestrictionType]:
  case Step(action: MigrationAction[TableType, DataType, RestrictionType])

  case Conditional(
      condition: MigrationCondition[TableType, DataType, RestrictionType],
      migration: Migration[TableType, DataType, RestrictionType],
    )

  case Sequence(left: Migration[TableType, DataType, RestrictionType], right: Migration[TableType, DataType, RestrictionType])
  case Transaction(migration: Migration[TableType, DataType, RestrictionType])

  def +[T >: TableType, D >: DataType, R >: RestrictionType](other: Migration[T, D, R]): Migration[T, D, R] =
    Sequence(this, other)

  def when[T >: TableType, D >: DataType, R >: RestrictionType](condition: MigrationCondition[T, D, R]): Migration[T, D, R] =
    Conditional(condition, this)

object Migration:

  def createTable(table: String): Migration[Nothing, Nothing, Nothing] = Step(MigrationAction.CreateTable(table))

  def addColumn[DataType, RestrictionType](
      table:        String,
      column:       String,
      dataType:     DataType,
      restrictions: RestrictionType*,
    ): Migration[Nothing, DataType, RestrictionType] =
    Step(MigrationAction.AddColumn(table, Column(column, dataType, restrictions)))

  def dropTable(table: String): Migration[Nothing, Nothing, Nothing] =
    Step(MigrationAction.DropTable(table))

  def dropColumn(table: String, column: String): Migration[Nothing, Nothing, Nothing] =
    Step(MigrationAction.DropColumn(table, column))

  def addIndex[DataType, RestrictionType](
      table: String,
      index: Index[DataType, RestrictionType],
    ): Migration[Nothing, DataType, RestrictionType] =
    Step(MigrationAction.AddIndex(table, index))

  def dropIndex(table: String, index: String): Migration[Nothing, Nothing, Nothing] =
    Step(MigrationAction.DropIndex(table, index))

  def addForeignKey(table: String, foreignKey: ForeignKey): Migration[Nothing, Nothing, Nothing] =
    Step(MigrationAction.AddForeignKey(table, foreignKey))

  def dropForeignKey(table: String, foreignKey: String): Migration[Nothing, Nothing, Nothing] =
    Step(MigrationAction.DropForeignKey(table, foreignKey))

enum MigrationAction[+TableType, +DataType, +RestrictionType]:
  case CreateTable(table: String)
  case DropTable(table: String)
  case AddColumn(table: String, column: Column[DataType, RestrictionType])
  case DropColumn(table: String, column: String)
  case AddIndex(table: String, index: Index[DataType, RestrictionType])
  case DropIndex(table: String, index: String)
  case AddForeignKey(table: String, foreignKey: ForeignKey)
  case DropForeignKey(table: String, foreignKey: String)

final case class Index[+DataType, +RestrictionType](
    name: String,
    columns: Seq[Column[DataType, RestrictionType]],
  )

final case class ForeignKey(
    name: String,
    columns: Seq[String],
    referencedTable: String,
    referencedColumns: Seq[String],
  )

enum MigrationCondition[+TableType, +DataType, +RestrictionType]:
  case TableExists()
  case ColumnExists(tableName: String, column: Column[DataType, RestrictionType])
  case IndexExists(index: String)
  case EnvironmentSet(environment: String)
  case EnvironmentValue(environment: String, value: String)
  case Not(condition: MigrationCondition[TableType, DataType, RestrictionType])
  case And(left: MigrationCondition[TableType, DataType, RestrictionType], right: MigrationCondition[TableType, DataType, RestrictionType])

  def &&[T >: TableType, D >: DataType, R >: RestrictionType](that: MigrationCondition[T, D, R]): MigrationCondition[T, D, R] =
    And(this, that)

  def ||[T >: TableType, D >: DataType, R >: RestrictionType](that: MigrationCondition[T, D, R]): MigrationCondition[T, D, R] =
    !(!this && !that)

  def unary_! : MigrationCondition[TableType, DataType, RestrictionType] =
    Not(this)

object Examples:

  val one: Migration[Nothing, String, String] = Migration.createTable("actor")
    + Migration.addColumn("actor", "id", "INTEGER", "primaryKeyName=actor_pkey", "nullable=false", "autoIncrement=true")
    + Migration.addColumn("actor", "firstname", "VARCHAR(255)")
    + Migration.addColumn("actor", "lastname", "VARCHAR(255)")
    + Migration.addColumn("actor", "twitter", "VARCHAR(15)")
