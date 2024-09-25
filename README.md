![logo](ldbc-logo.png)

# FinBench DataGen

![Build status](https://github.com/ldbc/ldbc_finbench_datagen/actions/workflows/ci.yml/badge.svg?branch=main)

The LDBC FinBench Data Generator (Datagen) produces the datasets for
the [LDBC FinBench's workloads](https://ldbcouncil.org/benchmarks/finbench/).

This data generator produces labelled directred property graphs based on the simulation of financial activities in
business systems. The key features include generation, factorization and transformation. A detailed description of the
schema produced by Datagen, as well as the format of the output files, can be found in the latest version of official
LDBC FinBench specification document.

## DataGen Design

### Data Schema

![Schema](./data-schema.png)

### Implementation

- Generation: Generation simulates financial activities in business systems to produce the raw data.
- Factorization: Factorization profiles of the raw data to produce factor tables used for further parameter curation.
- Transformation: Transformation transforms the raw data to the data for SUT and benchmark driver.

Note:

- Generation and Factorization are implemented in Scala while transformation is implemented in Python
  under `transformation/`.
- SUT stands for System Under Test.

## Quick Start

### Pre-requisites

- Java 8 installed.
- Python3 and related packages installed. See each `install-dependencies.sh` for details.
- Scala 2.12, note that it will be integrated when maven builds.
- Spark deployed. Spark 3.2.x is the recommended runtime to use. The rest of the instructions are provided assuming
  Spark 3.2.x.

### Workflow

- Use the spark application to generate the factor tables and raw data.
- Use the python scripts to transform the data to snapshot data and write queries.

### Generation of Raw Data and Factors

#### Local-Mode:

- Deploy Spark
    - use `scripts/get-spark-to-home.sh` to download pre-built spark to home directory and then decompress it.
    - Set the PATH environment variable to include the Spark binaries.
- Build the project
    - run `mvn clean package -DskipTests` to package the artifacts.
- Run locally with scripts
    - See `scripts/run_local.sh` for details. It uses spark-submit to run the data generator. Please make sure you have
      the pre-requisites installed and the build is successful.

- Generate Parameters
    - `cd ldbc_finbench_datagen/tools/paramgen` and run
    `python3 parameter_curation.py`

#### Cluster-Mode:

- Deploy Hadoop
    - use `wget https://downloads.apache.org/hadoop/common/hadoop-3.2.4/hadoop-3.2.4.tar.gz` to download hadoop correspond to this version of spark
    - `tar -xzvf hadoop-3.2.4.tar.gz` then `mv hadoop-3.2.4 /usr/local/hadoop`
- Set Environment Variable
    - `export HADOOP_HOME=/usr/local/hadoop` and 
      `export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin`. Be sure that `echo $JAVA_HOME`, `echo $HADOOP_HOME` and `echo $SPARK_HOME` have correct path.

- Set Master and Worker node
    - for all master and worker nodes. Add following settings to core-site.xml in `/usr/local/hadoop/etc/hadoop/core-site.xml`
    ```xml
    <configuration> 
      <property> 
        <name>fs.defaultFS</name> 
        <value>hdfs://master_node_ip:9000</value>  
      </property> 
    </configuration>
    ```
    and add following to `/usr/local/hadoop/etc/hadoop/hdfs-site.xml`
    ```xml
    <configuration> 
      <!-- set NameNode Replications (only for master node)--> 
      <property> 
        <name>dfs.replication</name> 
        <value>2</value> 
      </property> 
      
      <!-- NameNode Data Directory (only for master node)--> 
      <property> 
        <name>dfs.namenode.name.dir</name> 
        <value>file:///tmp/finbench-out/namenode</value> 
      </property> 
      
      <!-- DataNode Data Directory (for all worker nodes)--> 
      <property> 
        <name>dfs.datanode.data.dir</name> 
        <value>file:///tmp/finbench-out/datanode</value> 
      </property> 
    </configuration>
    ```
    modify the `/usr/local/hadoop/etc/hadoop/workers` in master node
    ```
    master_node_ip # Master
    worker_node_ip1 # Worker_1
    worker_node_ip2 # Worker_2
    ...
    ```
    then format the `HDFS` with `hdfs namenode -format`.
    Finally start the hadoop services with the script
    `/usr/local/hadoop/sbin/start-dfs.sh`

    - set the slaves in master node in `spark-3.2.2-bin-hadoop3.2/conf/slaves` 
    ```
    worker_node_ip1  # Worker_1
    worker_node_ip2  # Worker_2
    ...
    ```
    then run `start_master.sh` in master node in `spark-3.2.2-bin-hadoop3.2/sbin/` and run `start_worker.sh master_node_ip:7077` in every worker node.

- Build the Project and Run Spark script:
    - build the project in master node `mvn clean package -DskipTests`
    - run the script `bash script/run_cluster.sh`

- check the results
    - In master node use `hdfs dfs -ls /tmp/finbench-out/` check the generated data and factor tables 
    - and copy the factors to local file system: 
    `hdfs dfs -get /tmp/finbench-out/factor_table out/`

- Generate Parameters
    - `cd ldbc_finbench_datagen/tools/paramgen` and run
    `python3 parameter_curation.py`

- Run in cloud: To be supported

### Transformation of Raw Data

- set the `${FinBench_DATA_ROOT}` variable in `transformation/transform.sh` and run.

## TroubleShooting

N/A yet

# Related Work

- FinBench Specification: https://github.com/ldbc/ldbc_finbench_docs
- FinBench Driver: https://github.com/ldbc/ldbc_finbench_driver
- FinBench Reference Implementation: https://github.com/ldbc/ldbc_finbench_transaction_impls
- FinBench ACID Suite: https://github.com/ldbc/finbench-acid

 